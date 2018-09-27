
package org.openecomp.mso.asdc.util;

import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.Group;
import org.onap.sdc.toscaparser.api.NodeTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import static org.onap.sdc.tosca.parser.impl.SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID;

public class SoSdcCsarHelper {

//    private final Map<String, String> vfModuleCIDMap = new HashMap<>();
    private final ISdcCsarHelper delegate;

    private SoSdcCsarHelper(String csarFile) throws SdcToscaParserException {
        final SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance();
        delegate = factory.getSdcCsarHelper(csarFile);
    }

    public static ISdcCsarHelper createHelper(String csarFile) throws SdcToscaParserException {
        final SoSdcCsarHelper helper = new SoSdcCsarHelper(csarFile);
        return helper.createHelper();
    }

    private ISdcCsarHelper createHelper() {
        return (ISdcCsarHelper) Proxy.newProxyInstance(ISdcCsarHelper.class.getClassLoader(), new Class[]{ISdcCsarHelper.class}, this::handleProxy);
    }

    private Object handleProxy(Object o, Method method, Object[] params) {
        try {
            if (method.getName().equals("getVfModulesByVf")) {
                @SuppressWarnings("unchecked") final List<Group> delegateResult = (List<Group>) method.invoke(delegate, params);
                return delegateResult == null || delegateResult.size() == 0 ? handleVfModule(o, (String) params[0]) : delegateResult;
            }

            return method.invoke(delegate, params);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Group> handleVfModule(Object o, String vfCustomizationUuid) throws NoSuchFieldException, IllegalAccessException {
        final List<Group> result = new ArrayList<>();
        final List<NodeTemplate> serviceVfList = delegate.getServiceVfList();
        final Optional<NodeTemplate> nodeTemplateByCustomizationUuid = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationUuid);

        nodeTemplateByCustomizationUuid.ifPresent((nodeTemplate) -> {
            for (Group group : nodeTemplate.getSubMappingToscaTemplate().getGroups()) {
                try {
                    final Field tpl = group.getClass().getDeclaredField("tpl");
                    tpl.setAccessible(true);
                    final LinkedHashMap<String, Object> templatesMap = (LinkedHashMap<String, Object>) tpl.get(group);
                    final LinkedHashMap<String, Object> templates = new LinkedHashMap<>(templatesMap);
                    final Map<String, String> metadata = (Map<String, String>) templates.get("metadata");
                    final String modelUUID = delegate.getMetadataPropertyValue(group.getMetadata(), SdcPropertyNames.PROPERTY_NAME_VFMODULEMODELUUID);
                    metadata.put("vfModuleModelCustomizationUUID", modelUUID);
                    metadata.put("vfModuleCustomizationUUID", modelUUID);
                    result.add(new Group(group.getName(), templates, group.getMemberNodes(), group.getCustomDef()));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return result;
    }

    private Optional<NodeTemplate> getNodeTemplateByCustomizationUuid(List<NodeTemplate> nodeTemplates, String customizationId) {
        if (customizationId == null) {
            return Optional.empty();
        }

        Optional<NodeTemplate> findFirst = nodeTemplates.stream()
                .filter(x -> (x.getMetaData() != null &&
                        customizationId.equals(x.getMetaData().getValue(PROPERTY_NAME_CUSTOMIZATIONUUID))))
                .findFirst();

        return findFirst;
    }

    /*private String getVfModuleUUID(String modelUUID) {
        String cid = vfModuleCIDMap.get(modelUUID);
        if (cid == null) {
            synchronized (vfModuleCIDMap) {
                cid = vfModuleCIDMap.get(modelUUID);
                if (cid == null) {
                    cid = UUID.randomUUID().toString();
                    vfModuleCIDMap.put(modelUUID, cid);
                }
            }
        }

        return cid;
    }*/

}
