/*
 * Modifications copyright (C) 2019 Stefan Spiska (Vitasystems GmbH) and Hannover Medical School,
 *
 * This file is part of Project EHRbase
 *
 * Copyright (c) 2015 Christian Chevalley
 * This file is part of Project Ethercis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehrbase.client.building;

import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.creation.RMObjectCreator;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.archetyped.TemplateId;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.composition.Entry;
import com.nedap.archie.rm.composition.EventContext;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.rm.generic.PartyProxy;
import com.nedap.archie.rm.support.identification.ArchetypeID;
import com.nedap.archie.rm.support.identification.TerminologyId;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.xmlbeans.XmlObject;
import org.ehrbase.client.building.rmobjektskeletonbuilder.RmObjektSkeletonBuilder;
import org.ehrbase.client.introspect.TemplateIntrospect;
import org.ehrbase.client.introspect.config.RmIntrospectConfig;
import org.ehrbase.serialisation.util.SnakeCase;
import org.ehrbase.terminology.openehr.implementation.LocalizedTerminologies;
import org.openehr.schemas.v1.*;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class OptSkeletonBuilder {

    private static final ArchieRMInfoLookup RM_INFO_LOOKUP = ArchieRMInfoLookup.getInstance();
    private static final RMObjectCreator RM_CREATOR = new RMObjectCreator(RM_INFO_LOOKUP);
    private static final LocalizedTerminologies LOCALIZED_TERMINOLOGIES;

    static {
        try {
            LOCALIZED_TERMINOLOGIES = new LocalizedTerminologies();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Class, RmIntrospectConfig> configMap;


    private final Map<Class, RmObjektSkeletonBuilder> builderMap;

    public OptSkeletonBuilder() {
        configMap = TemplateIntrospect.buildConfigMap();
        builderMap = buildBuilderMap();
    }

    private static Map<Class, RmObjektSkeletonBuilder> buildBuilderMap() {
        Reflections reflections = new Reflections(RmObjektSkeletonBuilder.class.getPackage().getName());
        Set<Class<? extends RmObjektSkeletonBuilder>> configs = reflections.getSubTypesOf(RmObjektSkeletonBuilder.class);

        return configs.stream().map(c -> {
            try {
                return c.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toMap(RmObjektSkeletonBuilder::getXmlClass, c -> c));
    }


    private Object buildSkeletonForTerminalRmObjekts(XmlObject cpo) {

        RmObjektSkeletonBuilder rmObjektSkeletonBuilder = builderMap
                .entrySet()
                .stream()
                .filter(e -> e.getKey().isAssignableFrom(cpo.getClass()))
                .findAny()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new RuntimeException(String.format("No builder for {%s}", cpo.getClass())));
        return rmObjektSkeletonBuilder.getRmObjekt(cpo);
    }


    /**
     * Generate empty Rm from template
     *
     * @param opt
     * @return
     * @throws Exception
     */
    public RMObject generate(OPERATIONALTEMPLATE opt) {
        CARCHETYPEROOT def = opt.getDefinition();

        Object c = handleArchetypeRoot(opt, def, "");

        return (RMObject) c;
    }


    /**
     * @param opt
     * @param def
     * @param path
     * @return
     * @throws Exception
     */
    private Object handleArchetypeRoot(OPERATIONALTEMPLATE opt,
                                       CARCHETYPEROOT def, String path) {

        Map<String, String> termDef = new HashMap<>();
        // Keep term definition to map
        for (ARCHETYPETERM term : def.getTermDefinitionsArray()) {
            String code = term.getCode();
            for (StringDictionaryItem item : term.getItemsArray()) {
                if ("text".equals(item.getId())) {
                    // TODO currently keep only text , let's check that should
                    // we keep description?
                    termDef.put(code, item.getStringValue());
                }
            }
        }
        log.debug("CARCHETYPEROOT path= {}", path);

        // Load complex component
        return handleComplexObject(opt, def, termDef, path);
    }

    /**
     * Load complex component
     *
     * @param opt
     * @param ccobj
     * @param termDef
     * @return
     */
    private Object handleComplexObject(OPERATIONALTEMPLATE opt,
                                       CCOMPLEXOBJECT ccobj, Map<String, String> termDef,
                                       String path) {

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("template_id", opt.getTemplateId().getValue());


        String rmTypeName = ccobj.getRmTypeName();
        log.debug("rmTypeName={}:nodeId={}:ccobj={}", rmTypeName, ccobj.getNodeId(), ccobj);

        addNodeId(ccobj, termDef, valueMap);
        // Loop Create attributes
        CATTRIBUTE[] cattributes = ccobj.getAttributesArray();

        for (CATTRIBUTE attr : cattributes) {
            String pathloop = path + TemplateIntrospect.PATH_DIVIDER + attr.getRmAttributeName();
            COBJECT[] children = attr.getChildrenArray();
            String attrName = attr.getRmAttributeName();
            if (attr instanceof CSINGLEATTRIBUTE && !pathloop.endsWith("/name")) {
                if (children != null && children.length > 0) {
                    try {
                        COBJECT cobj = children[0];
                        if (children.length > 1) {
                            log.debug("Multiple children in CATTRIBUTE:{}", children.length);
                        }
                        Object attrValue = handleCObject(opt, cobj, termDef,
                                pathloop);
                        log.debug("attrName={}: attrValue={}", attrName, attrValue);
                        if (attrValue != null) {
                            valueMap.put(attr.getRmAttributeName(), attrValue);
                        }
                    } catch (Exception e) {
                        log.error(String.format("Cannot create attribute name %s on path %s", attrName, pathloop), e);

                    }
                }
            } else if (attr instanceof CMULTIPLEATTRIBUTE) {

                List<Object> container = new ArrayList<>();

                for (COBJECT cobj : children) {
                    try {

                        Object attrValue = handleCObject(opt, cobj, termDef,
                                pathloop);
                        log.debug("attrName={}: attrValue={}", attrName, attrValue);
                        if (attrValue != null) {
                            container.add(attrValue);
                        }

                    } catch (Exception e) {
                        log.error("Cannot create attribute name " + attrName + " on path " + pathloop, e);

                    }
                }
                log.debug("valueMap.put {} :{}", attr.getRmAttributeName(), container);
                valueMap.put(attr.getRmAttributeName(), container);

            }
        }


        if ("EVENT".equals(rmTypeName)) {
            rmTypeName = "POINT_EVENT";
        }

        Class<?> rmClass = RM_INFO_LOOKUP.getClass(rmTypeName);

        if (Pathable.class.isAssignableFrom(rmClass)) {

            handelNonTemplateFields(rmClass, valueMap);
        }


        Object obj;
        try {
            CComplexObject elementConstraint = new CComplexObject();
            elementConstraint.setRmTypeName(rmTypeName);
            obj = RM_CREATOR.create(elementConstraint);
            addFields(obj, valueMap, termDef, opt);
        } catch (Exception e) {
            obj = null;
            log.warn("Could not create instance of type:{} ,for nodeid={}, path:{} ,valueMap:{} ,details:{}", rmTypeName, ccobj.getNodeId(), path, valueMap, e.getMessage());
        }

        return obj;
    }

    private void handelNonTemplateFields(Class<?> rmClass, Map<String, Object> valueMap) {
        RmIntrospectConfig introspectConfig = configMap.get(rmClass);

        if (introspectConfig != null) {
            Arrays.stream(FieldUtils.getAllFields(rmClass))
                    .filter(f -> introspectConfig.getNonTemplateFields().contains(f.getName()))
                    .forEach(f -> {
                        try {

                            Object value;
                            if (f.getType().equals(PartyProxy.class)) {
                                value = new PartyIdentified();
                            } else if (List.class.isAssignableFrom(f.getType())) {
                                value = new ArrayList<>();
                                Class unwarap = unwarap(f);
                                ((List) value).add(unwarap.getConstructor().newInstance());
                            } else {
                                value = f.getType().getConstructor().newInstance();
                            }
                            valueMap.computeIfAbsent(new SnakeCase(f.getName()).camelToSnake(), k -> value);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            log.warn(e.getMessage());
                        }
                    });

        } else {
            log.debug("No RmIntrospectConfig for {}", rmClass);

        }
    }

    public Class unwarap(Field field) {
        if (List.class.isAssignableFrom(field.getType())) {
            Type actualTypeArgument = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            return ReflectionUtils.forName(actualTypeArgument.getTypeName(), this.getClass().getClassLoader());
        } else {
            return field.getType();
        }
    }

    private void addFields(Object obj, Map<String, Object> valueMap, Map<String, String> termDef, OPERATIONALTEMPLATE opt) {
        for (Map.Entry<String, Object> e : valueMap.entrySet()) {
            try {
                Object value = e.getValue();
                final List<Object> valueList;
                if (value instanceof List) {
                    valueList = (List) value;
                } else {
                    valueList = Collections.singletonList(value);
                }
                for (Object v : valueList) {
                    RM_CREATOR.addElementToListOrSetSingleValues(obj, e.getKey(), v);
                }
            } catch (Exception e2) {
                log.warn(e2.getMessage());
            }
        }

        if (obj instanceof Entry) {
            ((Entry) obj).setEncoding(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-8"));
        }
        if (obj instanceof Locatable && StringUtils.isBlank(((Locatable) obj).getName().getValue())){
            ((Locatable) obj).getName().setValue(valueMap.getOrDefault("name","").toString());
        }
        if (obj instanceof Composition) {
            Archetyped archetypeDetails = new Archetyped();
            archetypeDetails.setTemplateId(new TemplateId());
            archetypeDetails.getTemplateId().setValue(opt.getTemplateId().getValue());
            archetypeDetails.setRmVersion("1.0.4");
            archetypeDetails.setArchetypeId(new ArchetypeID(((Composition) obj).getArchetypeNodeId()));

            ((Composition) obj).setArchetypeDetails(archetypeDetails);

            if (((Composition) obj).getContext() == null) {
                EventContext context = new EventContext();
                context.setEndTime(new DvDateTime());
                context.setStartTime(new DvDateTime());
                context.setHealthCareFacility(new PartyIdentified(null, null, new ArrayList<>()));
                context.setParticipations(new ArrayList<>());
                context.getParticipations().add(new Participation());
                context.setSetting(new DvCodedText());
                ((Composition) obj).setContext(context);

            }
        }

        if (obj instanceof DvCodedText) {
            DvCodedText dvCodedText = (DvCodedText) obj;
            Optional<CodePhrase> defining_code = Optional.ofNullable(valueMap.get("defining_code"))
                    .map(o -> (CodePhrase) o);
            String value = defining_code
                    .map(CodePhrase::getCodeString)
                    .map(termDef::get)
                    .orElse(defining_code
                            .map(this::findByTerminologie).orElse(null));
            dvCodedText.setValue(value);
        }
    }

    public String findByTerminologie(CodePhrase codePhrase) {
        try {
            return LOCALIZED_TERMINOLOGIES.getDefault().terminology(codePhrase.getTerminologyId().getValue()).rubricForCode(codePhrase.getCodeString(), "en");
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void addNodeId(CCOMPLEXOBJECT ccobj, Map<String, String> termDef, Map<String, Object> valueMap) {

        String nodeId = ccobj.getNodeId();
        if (nodeId != null && nodeId.trim().length() > 0) {
            DvText txtName = null;
            // root node with archetype_id as node_id
            // TODO check if name is already defined?
            if (ccobj instanceof CARCHETYPEROOT) {
                log.debug("set archetype_node_id="
                        + ((CARCHETYPEROOT) ccobj).getArchetypeId()
                        .getValue());
                valueMap.put("archetype_node_id", ((CARCHETYPEROOT) ccobj)
                        .getArchetypeId().getValue());
                Optional<String> name = OptNameHelper.extractName((CARCHETYPEROOT) ccobj);
                String termName = name.orElse(termDef.get(nodeId));
                if (termName != null) {
                    txtName = new DvText(termName);
                    valueMap.put("name", txtName);
                } else {
                    log.warn("name not found for nodeId {}", nodeId);
                }
                valueMap.put("name", txtName);
            } else {
                log.debug("set archetype_node_id={}", nodeId);
                valueMap.put("archetype_node_id", nodeId);
                String termName = termDef.get(nodeId);
                if (termName != null) {
                    txtName = new DvText(termName);
                    valueMap.put("name", txtName);
                } else {
                    log.warn("name not found for nodeId {}", nodeId);
                }
            }

        }

    }


    private Object handleCObject(OPERATIONALTEMPLATE opt, COBJECT cobj, Map<String, String> termDef, String path) {

        log.debug("cobj={}:{}", cobj.getClass(), cobj.getRmTypeName());

        if (cobj instanceof CARCHETYPEROOT) {
            if (!((CARCHETYPEROOT) cobj).getArchetypeId().getValue().isEmpty()) {
                path = path + "[" + ((CARCHETYPEROOT) cobj).getArchetypeId().getValue() + "]";
            }
            log.debug("CARCHETYPEROOT path={}", path);
            return handleArchetypeRoot(opt, (CARCHETYPEROOT) cobj, path);
        } else if (cobj instanceof CDOMAINTYPE) {
            return buildSkeletonForTerminalRmObjekts((CDOMAINTYPE) cobj);
        } else if (cobj instanceof CCOMPLEXOBJECT) {
            // Skip when path is /category and /context
            if ("/category".equalsIgnoreCase(path)) {
                return handleComplexObject(opt, (CCOMPLEXOBJECT) cobj, termDef, path);
            } else if ("/context".equalsIgnoreCase(path)) {
                return handleComplexObject(opt, (CCOMPLEXOBJECT) cobj, termDef, path);
            }
            if (!cobj.getNodeId().isEmpty()) {
                path = path + "[" + cobj.getNodeId() + "]";
            }
            log.debug("CONTEXT path={}", path);
            return handleComplexObject(opt, (CCOMPLEXOBJECT) cobj, termDef, path);
        } else if (cobj instanceof ARCHETYPESLOT) {
            if (!cobj.getNodeId().isEmpty()) {
                path = path + "[" + cobj.getNodeId() + "]";
            }
            log.debug("ARCHETYPESLOT path={}", path);
            return null;

        } else if (cobj instanceof CPRIMITIVEOBJECT) {
            return buildSkeletonForTerminalRmObjekts(cobj);
        } else {
            if (cobj.getNodeId() == null) {
                log.debug("NodeId is null : {}", cobj);
                return null;
            }
            log.debug("Some value cannot process because is not CARCHETYPEROOT or CCOMPLEXOBJECT : {}", cobj);

            return null;

        }

    }

}
