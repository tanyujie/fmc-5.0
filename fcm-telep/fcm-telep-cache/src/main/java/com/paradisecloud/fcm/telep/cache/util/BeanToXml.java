//package com.paradisecloud.fcm.telep.cache;
//
//
//
//
//import com.paradisecloud.fcm.telep.cache.util.JaxbMapper;
//import com.paradisecloud.fcm.telep.model.*;
//import com.paradisecloud.fcm.telep.model.ClassValue;
//
//
//import java.util.ArrayList;
//
//
///**
// * @author nj
// * @date 2022/10/10 16:36
// */
//public class BeanToXml {
//    public static void main(String[] args) {
//
//        MethodCall methodCall = new MethodCall();
//
//        ParamClass paramClass = new ParamClass();
//
//        ClassParam classParam = new ClassParam();
//        ClassValue classValue = new ClassValue();
//        ClassStruct classStruct = new ClassStruct();
//
//        ArrayList<Member> memberArrayList = new ArrayList<>();
//
//        Member member1 = new Member();
//        Value value1 = new Value();
//
//
//        member1.setName("authenticationUser");
//        value1.setStringValue("admin");
//        member1.setValue(value1);
//
//        Member member2 = new Member();
//        Value value2 = new Value();
//
//        member2.setName("authenticationPassword");
//        value2.setStringValue("cisco");
//        member2.setValue(value2);
//
//        Member member3 = new Member();
//        Value value3 = new Value();
//
//
//        member3.setName("conferenceName");
//        value3.setStringValue("7811sdfs229");
//        member3.setValue(value3);
//
//        Member member4 = new Member();
//        Value value4 = new Value();
//
//        member4.setName("startTime");
//        value4.setDateTimeValue("20221011T01:45:00");
//        member4.setValue(value4);
//
//
//        memberArrayList.add(member1);
//        memberArrayList.add(member2);
//        memberArrayList.add(member3);
//        memberArrayList.add(member4);
//
//        classStruct.setMember(memberArrayList);
//        classValue.setClassStruct(classStruct);
//        classParam.setClassValue(classValue);
//        paramClass.setClassParam(classParam);
//        methodCall.setParamClass(paramClass);
//        methodCall.setMethodName("conference.create");
//        String s = JaxbMapper.toXml(methodCall, MethodCall.class, "utf-8");
//        System.out.println(s);
//
//
//        String s1 = ClientAuthentication.postXml("http://172.16.100.136/RPC2", s);
//        System.out.println(s1);
//
//        MethodResponse methodResponse = JaxbMapper.fromXml(s1, MethodResponse.class);
//        System.out.println(methodResponse);
//
//
//
//
//
//    }
//}
