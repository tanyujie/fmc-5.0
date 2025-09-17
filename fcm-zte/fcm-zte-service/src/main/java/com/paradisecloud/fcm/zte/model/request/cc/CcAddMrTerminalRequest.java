package com.paradisecloud.fcm.zte.model.request.cc;


import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CcAddMrTerminalRequest extends CommonRequest {
    private String ConferenceIdentifier;
    private String ConferencePassword;
    private Participant participant;
    private int ConferenceIdOption;

    public String getConferenceIdentifier() {
        return ConferenceIdentifier;
    }

    public void setConferenceIdentifier(String conferenceIdentifier) {
        ConferenceIdentifier = conferenceIdentifier;
    }

    public String getConferencePassword() {
        return ConferencePassword;
    }

    public void setConferencePassword(String conferencePassword) {
        ConferencePassword = conferencePassword;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public int getConferenceIdOption() {
        return ConferenceIdOption;
    }

    public void setConferenceIdOption(int conferenceIdOption) {
        ConferenceIdOption = conferenceIdOption;
    }

    public static class Participant{
        private String TerminalName;
        private String TerminalNumber;
        private String TerminalIdentifier;
        private int TerType;
        private int CallMode;
        private String IpAddress;
        private int CascadePortType;


        public String getTerminalName() {
            return TerminalName;
        }

        public void setTerminalName(String terminalName) {
            TerminalName = terminalName;
        }

        public String getTerminalNumber() {
            return TerminalNumber;
        }

        public void setTerminalNumber(String terminalNumber) {
            TerminalNumber = terminalNumber;
        }

        public String getTerminalIdentifier() {
            return TerminalIdentifier;
        }

        public void setTerminalIdentifier(String terminalIdentifier) {
            TerminalIdentifier = terminalIdentifier;
        }

        public int getTerType() {
            return TerType;
        }

        public void setTerType(int terType) {
            TerType = terType;
        }

        public int getCallMode() {
            return CallMode;
        }

        public void setCallMode(int callMode) {
            CallMode = callMode;
        }

        public String getIpAddress() {
            return IpAddress;
        }

        public void setIpAddress(String ipAddress) {
            IpAddress = ipAddress;
        }

        public int getCascadePortType() {
            return CascadePortType;
        }

        public void setCascadePortType(int cascadePortType) {
            CascadePortType = cascadePortType;
        }
    }





    @Override
    public String buildToXml() {
        String xml = "";
        return xml;
    }
}
