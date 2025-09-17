/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeInvoker.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.fmeinvoker
 * @author lilinhai
 * @since 2021-01-28 18:42
 * @version  V1.0
 */
package com.paradisecloud.fcm.telep.cache.invoker;


import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.telep.cache.exception.FaultException;
import com.paradisecloud.fcm.telep.cache.util.JaxbMapper;
import com.paradisecloud.fcm.telep.model.ClassValue;
import com.paradisecloud.fcm.telep.model.*;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <pre>FME调用器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-01-28 18:42
 */
public abstract class TelePApiInvoker {


    protected String rootUrl;

    protected XmlRpcLocalRequest xmlRpcLocalRequest;


    public TelePApiInvoker(String rootUrl, XmlRpcLocalRequest xmlRpcLocalRequest) {
        this.rootUrl = rootUrl;
        this.xmlRpcLocalRequest = xmlRpcLocalRequest;
    }

    protected void faultMessage(MethodResponse methodResponse) {
        if (methodResponse.getClassFault() != null) {
            List<Member> member1 = methodResponse.getClassFault().getClassValue().getClassStruct().getMember();
            Optional<Member> faultString = member1.stream().filter(p -> Objects.equals(p.getName(), "faultString")).findFirst();
            if (faultString.isPresent()) {
                String stringValue = faultString.get().getValue().getStringValue();
                throw new FaultException(stringValue);
            } else {
                throw new FaultException("tele请求错误");
            }

        }
    }

    protected <T> T getEntityPlus(String xml, Class<T> type) {
        if (StringUtils.isEmpty(xml)) {
            return null;
        }
        MethodResponse methodResponse = JaxbMapper.fromXml(xml, MethodResponse.class);
        faultMessage(methodResponse);

        List<Member> responseMembers = getResponseMembers(methodResponse);
        if (!StringUtils.isEmpty(responseMembers)) {
            try {
                return convertPlus(type, responseMembers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }


    protected <T> T getEntity(String xml, Class<T> type, String ListName) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (StringUtils.isEmpty(xml)) {
            return null;
        }
        MethodResponse methodResponse = JaxbMapper.fromXml(xml, MethodResponse.class);
        faultMessage(methodResponse);

        List<Member> responseMembers = getResponseMembers(methodResponse);
        if (StringUtils.isEmpty(ListName)) {
            return convert(type, responseMembers);
        }
        return null;

    }


    protected <T> List<T> getEntityList(String xml, Class<T> type, String ListName) {
        if (StringUtils.isEmpty(xml)) {
            return null;
        }
        MethodResponse methodResponse = JaxbMapper.fromXml(xml, MethodResponse.class);
        faultMessage(methodResponse);
        List<Member> responseMembers = getResponseMembers(methodResponse);
        List<T> result = new ArrayList<>();
        for (Member responseMember : responseMembers) {
            if (Objects.equals(responseMember.getName(), ListName)) {

                List<DataValue> dataValueList = responseMember.getValue().getDataValueList();

                for (DataValue dataValue : dataValueList) {
                    List<ClassValue> classValue = dataValue.getClassParam().getClassValue();
                    for (ClassValue value : classValue) {
                        List<Member> members = value.getClassStruct().getMember();

                        T convert = null;
                        try {
                            convert = convert(type, members);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        result.add(convert);
                    }

                }
            }
        }
        return result;
    }

    private <T> T convert(Class<T> type, List<Member> responseMembers) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T obj = type.getDeclaredConstructor().newInstance();
        if (CollectionUtils.isNotEmpty(responseMembers)) {
            responseMembers.stream().forEach(p -> {
                Field[] fields = type.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (Objects.equals(p.getName(), field.getName())) {
                        Value value = p.getValue();
                        Class<?> curFieldType = field.getType();
                        String fieldType = field.getType().toString();
                        if (Objects.equals(fieldType, "class java.lang.String")) {
                            String stringValue = value.getStringValue();
                            if (org.apache.commons.lang3.StringUtils.isBlank(stringValue)) {
                                stringValue = value.getDateTimeValue();
                            }
                            try {
                                field.set(obj, stringValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (curFieldType.equals(Integer.class) || curFieldType.equals(int.class)) {
                            int intValue = value.getIntValue();
                            try {
                                field.set(obj, intValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (Objects.equals(fieldType, "class java.lang.Long")) {
                            Long longValue = value.getLongValue();
                            try {
                                field.set(obj, longValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (Objects.equals(fieldType, "class java.lang.Boolean")) {
                            Boolean booleanValue = value.getBooleanValue();
                            try {
                                field.set(obj, booleanValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

            });
        }
        return obj;
    }

    private <T> T convertPlus(Class<T> type, List<Member> responseMembers) throws Exception {
        T obj = type.getDeclaredConstructor().newInstance();
        if (CollectionUtils.isNotEmpty(responseMembers)) {
            responseMembers.stream().forEach(p -> {
                Field[] fields = type.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (Objects.equals(p.getName(), field.getName())) {
                        Value value = p.getValue();
                        Class<?> curFieldType = field.getType();
                        String fieldType = field.getType().toString();
                        if (Objects.equals(fieldType, "class java.lang.String")) {
                            String stringValue = value.getStringValue();
                            if (org.apache.commons.lang3.StringUtils.isBlank(stringValue)) {
                                stringValue = value.getDateTimeValue();
                            }
                            try {
                                field.set(obj, stringValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (curFieldType.equals(Integer.class) || curFieldType.equals(int.class)) {
                            int stringValue = value.getIntValue();
                            try {
                                field.set(obj, stringValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (Objects.equals(fieldType, "class java.lang.Long")) {
                            Long stringValue = value.getLongValue();
                            try {
                                field.set(obj, stringValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (Objects.equals(fieldType, "class java.lang.Boolean")) {
                            Boolean stringValue = value.getBooleanValue();
                            try {
                                field.set(obj, stringValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (curFieldType.equals(List.class)) {


                            Type genericType = field.getGenericType();
                            if (null == genericType) {
                                continue;
                            }
                            if (genericType instanceof ParameterizedType) {
                                ParameterizedType pt = (ParameterizedType) genericType;
                                Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                                List<Object> arrayList = new ArrayList<>();

                                List<DataValue> dataValueList = p.getValue().getDataValueList();
                                for (DataValue dataValue : dataValueList) {

                                    List<ClassValue> classValue = dataValue.getClassParam().getClassValue();
                                    for (ClassValue classValue1 : classValue) {
                                        List<Member> members = classValue1.getClassStruct().getMember();
                                        Object typeArg = null;
                                        try {
                                            typeArg = convert(actualTypeArgument, members);
                                        } catch (InstantiationException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (NoSuchMethodException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        }
                                        arrayList.add(typeArg);
                                    }


                                }
                                try {
                                    field.set(obj, arrayList);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    }

                }

            });
        }
        return obj;
    }

    private List<Member> getResponseMembers(MethodResponse methodResponse) {
        return methodResponse.getParamClass().getClassParam().getClassValue().get(0).getClassStruct().getMember();
    }


}
