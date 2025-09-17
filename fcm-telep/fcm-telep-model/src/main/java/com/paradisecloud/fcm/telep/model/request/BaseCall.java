package com.paradisecloud.fcm.telep.model.request;

import com.paradisecloud.fcm.telep.model.*;
import com.paradisecloud.fcm.telep.model.ClassValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author nj
 * @date 2022/10/11 15:46
 */
public class BaseCall {

    public static MethodCall createBaseCall(String methodName) {


        MethodCall methodCall = new MethodCall();

        ParamClass paramClass = new ParamClass();

        ClassParam classParam = new ClassParam();
        ClassValue classValue = new ClassValue();
        ClassStruct classStruct = new ClassStruct();

        ArrayList<Member> memberArrayList = new ArrayList<>();

        Member member1 = new Member();
        Value value1 = new Value();


        member1.setName("authenticationUser");
        value1.setStringValue("admin");
        member1.setValue(value1);

        Member member2 = new Member();
        Value value2 = new Value();

        member2.setName("authenticationPassword");
        value2.setStringValue("cisco");
        member2.setValue(value2);


        memberArrayList.add(member1);
        memberArrayList.add(member2);


        classStruct.setMember(memberArrayList);
        classValue.setClassStruct(classStruct);
        classParam.setClassValue(Arrays.asList(classValue));
        paramClass.setClassParam(classParam);
        methodCall.setParamClass(paramClass);
        methodCall.setMethodName(methodName);
        return methodCall;
    }

    public static List<Member> getMembers(MethodCall methodCall) {
        return methodCall.getParamClass().getClassParam().getClassValue().get(0).getClassStruct().getMember();
    }
}
