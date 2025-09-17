package com.paradisecloud.fcm.zte.model.response.cc;

import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class CcGetTerminalInfoResponse extends CommonResponse {
    private String id;
    private String audio_in_protocol;
    private Integer audio_in_bit_rate;
    private Integer audio_in_packet_loss;
    private Integer audio_in_jitter;
    private String audio_out_protocol;
    private Integer audio_out_bit_rate;
    private Integer audio_out_packet_loss;
    private Integer audio_out_jitter;
    private Integer video_in_bit_rate;
    private String video_in_protocol;
    private Integer video_in_packet_loss;
    private Integer video_in_jitter;
    private String video_in_resolution;
    private Integer video_in_video_frame_rate;
    private Integer video_out_bit_rate;
    private String video_out_protocol;
    private Integer video_out_packet_loss;
    private Integer video_out_jitter;
    private String video_out_resolution;
    private Integer video_out_video_frame_rate;

    private Integer video_content_in_bit_rate;
    private String video_content_in_protocol;
    private Integer video_content_in_packet_loss;
    private Integer video_content_in_jitter;
    private String video_content_in_resolution;
    private Integer video_content_in_video_frame_rate;
    private Integer video_content_out_bit_rate;
    private String video_content_out_protocol;
    private Integer video_content_out_packet_loss;
    private Integer video_content_out_jitter;
    private String video_content_out_resolution;
    private Integer video_content_out_video_frame_rate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAudio_in_protocol() {
        return audio_in_protocol;
    }

    public void setAudio_in_protocol(String audio_in_protocol) {
        this.audio_in_protocol = audio_in_protocol;
    }

    public Integer getAudio_in_bit_rate() {
        return audio_in_bit_rate;
    }

    public void setAudio_in_bit_rate(Integer audio_in_bit_rate) {
        this.audio_in_bit_rate = audio_in_bit_rate;
    }

    public Integer getAudio_in_packet_loss() {
        return audio_in_packet_loss;
    }

    public void setAudio_in_packet_loss(Integer audio_in_packet_loss) {
        this.audio_in_packet_loss = audio_in_packet_loss;
    }

    public Integer getAudio_in_jitter() {
        return audio_in_jitter;
    }

    public void setAudio_in_jitter(Integer audio_in_jitter) {
        this.audio_in_jitter = audio_in_jitter;
    }

    public String getAudio_out_protocol() {
        return audio_out_protocol;
    }

    public void setAudio_out_protocol(String audio_out_protocol) {
        this.audio_out_protocol = audio_out_protocol;
    }

    public Integer getAudio_out_bit_rate() {
        return audio_out_bit_rate;
    }

    public void setAudio_out_bit_rate(Integer audio_out_bit_rate) {
        this.audio_out_bit_rate = audio_out_bit_rate;
    }

    public Integer getAudio_out_packet_loss() {
        return audio_out_packet_loss;
    }

    public void setAudio_out_packet_loss(Integer audio_out_packet_loss) {
        this.audio_out_packet_loss = audio_out_packet_loss;
    }

    public Integer getAudio_out_jitter() {
        return audio_out_jitter;
    }

    public void setAudio_out_jitter(Integer audio_out_jitter) {
        this.audio_out_jitter = audio_out_jitter;
    }

    public Integer getVideo_in_bit_rate() {
        return video_in_bit_rate;
    }

    public void setVideo_in_bit_rate(Integer video_in_bit_rate) {
        this.video_in_bit_rate = video_in_bit_rate;
    }

    public String getVideo_in_protocol() {
        return video_in_protocol;
    }

    public void setVideo_in_protocol(String video_in_protocol) {
        this.video_in_protocol = video_in_protocol;
    }

    public Integer getVideo_in_packet_loss() {
        return video_in_packet_loss;
    }

    public void setVideo_in_packet_loss(Integer video_in_packet_loss) {
        this.video_in_packet_loss = video_in_packet_loss;
    }

    public Integer getVideo_in_jitter() {
        return video_in_jitter;
    }

    public void setVideo_in_jitter(Integer video_in_jitter) {
        this.video_in_jitter = video_in_jitter;
    }

    public String getVideo_in_resolution() {
        return video_in_resolution;
    }

    public void setVideo_in_resolution(String video_in_resolution) {
        this.video_in_resolution = video_in_resolution;
    }

    public Integer getVideo_in_video_frame_rate() {
        return video_in_video_frame_rate;
    }

    public void setVideo_in_video_frame_rate(Integer video_in_video_frame_rate) {
        this.video_in_video_frame_rate = video_in_video_frame_rate;
    }

    public Integer getVideo_out_bit_rate() {
        return video_out_bit_rate;
    }

    public void setVideo_out_bit_rate(Integer video_out_bit_rate) {
        this.video_out_bit_rate = video_out_bit_rate;
    }

    public String getVideo_out_protocol() {
        return video_out_protocol;
    }

    public void setVideo_out_protocol(String video_out_protocol) {
        this.video_out_protocol = video_out_protocol;
    }

    public Integer getVideo_out_packet_loss() {
        return video_out_packet_loss;
    }

    public void setVideo_out_packet_loss(Integer video_out_packet_loss) {
        this.video_out_packet_loss = video_out_packet_loss;
    }

    public Integer getVideo_out_jitter() {
        return video_out_jitter;
    }

    public void setVideo_out_jitter(Integer video_out_jitter) {
        this.video_out_jitter = video_out_jitter;
    }

    public String getVideo_out_resolution() {
        return video_out_resolution;
    }

    public void setVideo_out_resolution(String video_out_resolution) {
        this.video_out_resolution = video_out_resolution;
    }

    public Integer getVideo_out_video_frame_rate() {
        return video_out_video_frame_rate;
    }

    public void setVideo_out_video_frame_rate(Integer video_out_video_frame_rate) {
        this.video_out_video_frame_rate = video_out_video_frame_rate;
    }

    public Integer getVideo_content_in_bit_rate() {
        return video_content_in_bit_rate;
    }

    public void setVideo_content_in_bit_rate(Integer video_content_in_bit_rate) {
        this.video_content_in_bit_rate = video_content_in_bit_rate;
    }

    public String getVideo_content_in_protocol() {
        return video_content_in_protocol;
    }

    public void setVideo_content_in_protocol(String video_content_in_protocol) {
        this.video_content_in_protocol = video_content_in_protocol;
    }

    public Integer getVideo_content_in_packet_loss() {
        return video_content_in_packet_loss;
    }

    public void setVideo_content_in_packet_loss(Integer video_content_in_packet_loss) {
        this.video_content_in_packet_loss = video_content_in_packet_loss;
    }

    public Integer getVideo_content_in_jitter() {
        return video_content_in_jitter;
    }

    public void setVideo_content_in_jitter(Integer video_content_in_jitter) {
        this.video_content_in_jitter = video_content_in_jitter;
    }

    public String getVideo_content_in_resolution() {
        return video_content_in_resolution;
    }

    public void setVideo_content_in_resolution(String video_content_in_resolution) {
        this.video_content_in_resolution = video_content_in_resolution;
    }

    public Integer getVideo_content_in_video_frame_rate() {
        return video_content_in_video_frame_rate;
    }

    public void setVideo_content_in_video_frame_rate(Integer video_content_in_video_frame_rate) {
        this.video_content_in_video_frame_rate = video_content_in_video_frame_rate;
    }

    public Integer getVideo_content_out_bit_rate() {
        return video_content_out_bit_rate;
    }

    public void setVideo_content_out_bit_rate(Integer video_content_out_bit_rate) {
        this.video_content_out_bit_rate = video_content_out_bit_rate;
    }

    public String getVideo_content_out_protocol() {
        return video_content_out_protocol;
    }

    public void setVideo_content_out_protocol(String video_content_out_protocol) {
        this.video_content_out_protocol = video_content_out_protocol;
    }

    public Integer getVideo_content_out_packet_loss() {
        return video_content_out_packet_loss;
    }

    public void setVideo_content_out_packet_loss(Integer video_content_out_packet_loss) {
        this.video_content_out_packet_loss = video_content_out_packet_loss;
    }

    public Integer getVideo_content_out_jitter() {
        return video_content_out_jitter;
    }

    public void setVideo_content_out_jitter(Integer video_content_out_jitter) {
        this.video_content_out_jitter = video_content_out_jitter;
    }

    public String getVideo_content_out_resolution() {
        return video_content_out_resolution;
    }

    public void setVideo_content_out_resolution(String video_content_out_resolution) {
        this.video_content_out_resolution = video_content_out_resolution;
    }

    public Integer getVideo_content_out_video_frame_rate() {
        return video_content_out_video_frame_rate;
    }

    public void setVideo_content_out_video_frame_rate(Integer video_content_out_video_frame_rate) {
        this.video_content_out_video_frame_rate = video_content_out_video_frame_rate;
    }

    /**
     *
     * <RESPONSE_TRANS_PARTY>
     *     <RETURN_STATUS>
     *         <ID>0</ID>
     *         <DESCRIPTION>Status OK</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>2</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <GET>
     *             <ONGOING_PARTY>
     *                 <PARTY>
     *                     <NAME>lxl_418948995_(006)</NAME>
     *                     <ID>7</ID>
     *                     <INTERFACE>sip</INTERFACE>
     *                     <NET_CHANNEL_NUMBER>auto</NET_CHANNEL_NUMBER>
     *                     <ENDPOINT_MEDIA_TYPE>avc</ENDPOINT_MEDIA_TYPE>
     *                 </PARTY>
     *                 <LOCAL_COMM_MODE></LOCAL_COMM_MODE>
     *                 <REMOTE_COMM_MODE></REMOTE_COMM_MODE>
     *                 <REMOTE_CAP>
     * ---Audio---------
     * G.711 A-law/ -law
     * ---Video---------
     * NONE
     * ---Restrict Cap-----
     * Restrict or Non-Restrict
     *
     * ---capabilities----
     * </REMOTE_CAP>
     *                 <H323_LOCAL_COMM_MODE>G7221C_48k - 0 frames per packet
     * H264 - 1000000 bps
     *   0 frame rate per second
     *   264Mode: standard
     *   High Profile
     *   Level 3.1
     *   CustomMaxFS at 15 (3840 MBs)
     * H264 (Transmit) - 0 bps
     *   H.239 Role: Presentation
     *   0 frame rate per second
     *   264Mode: standard
     *   High Profile
     *   Level 3.1
     *   CustomMaxMBPS at 245 (122500 MB/s)
     *   CustomMaxFS at 32 (8192 MBs)
     * BFCP
     *  setup         passive
     *  connection    new
     *  floor control s_only
     *  confid        1
     *  userid        2
     *  FloorID       1
     *  Stream0       21
     *  FloorID
     *  Stream1
     *  FloorID
     *  Stream2
     *  FloorID
     *  Stream3
     *  info enabled  0
     *  info time     0
     * LPR -
     *   versionID           2
     *   minProtectionPeriod 150
     *   maxProtectionPeriod 150
     *   maxRecoverySet      52
     *   maxRecoveryPackets  10
     *   maxPacketSize       1260
     * </H323_LOCAL_COMM_MODE>
     *                 <H323_REMOTE_COMM_MODE>G7221C_48k - 0 frames per packet
     * H264 - 1048000 bps
     *   0 frame rate per second
     *   264Mode: standard
     *   High Profile
     *   Level 3.1
     *   CustomMaxFS at 15 (3840 MBs)
     * H264 - 0 bps
     *   H.239 Role: Presentation
     *   0 frame rate per second
     *   264Mode: standard
     *   High Profile
     *   Level 3.1
     *   CustomMaxMBPS at 245 (122500 MB/s)
     *   CustomMaxFS at 32 (8192 MBs)
     * BFCP
     *  setup         passive
     *  connection    new
     *  floor control s_only
     *  confid        1
     *  userid        2
     *  FloorID       1
     *  Stream0       21
     *  FloorID
     *  Stream1
     *  FloorID
     *  Stream2
     *  FloorID
     *  Stream3
     *  info enabled  0
     *  info time     0
     * </H323_REMOTE_COMM_MODE>
     *                 <H323_REMOTE_CAP>eOpus_64kCapCode - 0 frames per packet
     *   RTP payload type 107
     * G7221C_48k - 0 frames per packet
     *   RTP payload type 121
     * G7221C_32k - 0 frames per packet
     *   RTP payload type 122
     * G7221C_24k - 0 frames per packet
     *   RTP payload type 123
     * G7221_24k - 0 frames per packet
     *   RTP payload type 124
     * G722_64k - 0 milli packet duration
     * G711Ulaw64k - 0 milli packet duration
     * G711Alaw64k - 0 milli packet duration
     * Rfc2833Dtmf -
     *  RTP payload type 101
     * H264 - 1048500 bps
     *   0 frame rate per second
     *   264Mode: standard
     *   High Profile
     *   Level 3.2
     *   CustomMaxMBPS at 308 (154000 MB/s)
     *   CustomMaxFS at 20 (5120 MBs)
     * H264 - 1048500 bps
     *   0 frame rate per second
     *   264Mode: standard
     *   BaseLine Profile
     *   Level 3.2
     *   CustomMaxMBPS at 308 (154000 MB/s)
     *   CustomMaxFS at 20 (5120 MBs)
     * H264 - 1048500 bps
     *   0 frame rate per second
     *   264Mode: standard
     *   BaseLine Profile
     *   Level 3.2
     *   CustomMaxMBPS at 308 (154000 MB/s)
     *   CustomMaxFS at 20 (5120 MBs)
     * H263 - 1048500 bps
     *   4CIF at 30 fps
     *   CIF at 30 fps
     *   QCIF at 30 fps
     * H264 - 1048500 bps
     *   H.239 Role: Presentation
     *   0 frame rate per second
     *   264Mode: standard
     *   High Profile
     *   Level 4.2
     *   CustomMaxMBPS at 523 (261500 MB/s)
     *   CustomMaxFS at 34 (8704 MBs)
     * H264 - 1048500 bps
     *   H.239 Role: Presentation
     *   0 frame rate per second
     *   264Mode: standard
     *   BaseLine Profile
     *   Level 4.2
     *   CustomMaxMBPS at 523 (261500 MB/s)
     *   CustomMaxFS at 34 (8704 MBs)
     * H264 - 1048500 bps
     *   H.239 Role: Presentation
     *   0 frame rate per second
     *   264Mode: standard
     *   BaseLine Profile
     *   Level 4.2
     *   CustomMaxMBPS at 523 (261500 MB/s)
     *   CustomMaxFS at 34 (8704 MBs)
     * H263 - 1048500 bps
     *   H.239 Role: Presentation
     *   4CIF at 30 fps
     *   CIF at 30 fps
     *   QCIF at 30 fps
     * BFCP
     *  setup         active
     *  connection    new
     *  floor control c_only
     *  confid        1
     *  userid        2
     *  FloorID       1
     *  Stream0       3
     *  FloorID
     *  Stream1
     *  FloorID
     *  Stream2
     *  FloorID
     *  Stream3
     *  info enabled  0
     *  info time     0
     * </H323_REMOTE_CAP>
     *                 <IP_MONITOR_CHANNELS>
     *                     <IP_MONITOR_CHANNEL>
     *                         <BASIC_PARAM>
     *                             <MAP_PROBLEM/>
     *                             <BIT_RATE>-1</BIT_RATE>
     *                             <PROTOCOL>UnknownAlgorithm</PROTOCOL>
     *                             <CHANNEL_INDEX>0</CHANNEL_INDEX>
     *                             <CHANNEL_TYPE>h225</CHANNEL_TYPE>
     *                             <PARTY_ADDRESS>172.16.100.66</PARTY_ADDRESS>
     *                             <MCU_ADDRESS>172.16.100.70</MCU_ADDRESS>
     *                             <PARTY_PORT>5060</PARTY_PORT>
     *                             <MCU_PORT>5060</MCU_PORT>
     *                             <PARTY_TRANSPORT_TYPE>tcp</PARTY_TRANSPORT_TYPE>
     *                             <MCU_TRANSPORT_TYPE>tcp</MCU_TRANSPORT_TYPE>
     *                             <CONNECTION_STATUS>1</CONNECTION_STATUS>
     *                             <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                             <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                             <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                             <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                             <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                             <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                             <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                         </BASIC_PARAM>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <BASIC_PARAM>
     *                             <MAP_PROBLEM/>
     *                             <BIT_RATE>-1</BIT_RATE>
     *                             <PROTOCOL>UnknownAlgorithm</PROTOCOL>
     *                             <CHANNEL_INDEX>0</CHANNEL_INDEX>
     *                             <CHANNEL_TYPE>h245</CHANNEL_TYPE>
     *                             <PARTY_ADDRESS>172.16.100.66</PARTY_ADDRESS>
     *                             <MCU_ADDRESS>172.16.100.70</MCU_ADDRESS>
     *                             <PARTY_PORT>5060</PARTY_PORT>
     *                             <MCU_PORT>5060</MCU_PORT>
     *                             <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                             <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                             <CONNECTION_STATUS>1</CONNECTION_STATUS>
     *                             <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                             <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                             <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                             <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                             <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                             <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                             <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                         </BASIC_PARAM>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <ADVANCED_AUDIO_IN>
     *                             <ADVANCED_AUDIO_PARAM>
     *                                 <GLOBAL_PARAM>
     *                                     <BASIC_PARAM>
     *                                         <MAP_PROBLEM/>
     *                                         <BIT_RATE>48000</BIT_RATE>
     *                                         <PROTOCOL>g7221C_48k</PROTOCOL>
     *                                         <CHANNEL_INDEX>1</CHANNEL_INDEX>
     *                                         <CHANNEL_TYPE>audio_in</CHANNEL_TYPE>
     *                                         <PARTY_ADDRESS>172.16.100.66</PARTY_ADDRESS>
     *                                         <MCU_ADDRESS>172.16.100.70</MCU_ADDRESS>
     *                                         <PARTY_PORT>50068</PARTY_PORT>
     *                                         <MCU_PORT>49390</MCU_PORT>
     *                                         <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                                         <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                                         <CONNECTION_STATUS>1</CONNECTION_STATUS>
     *                                         <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                                         <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                                         <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                                         <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                                         <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                                         <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                                         <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                                     </BASIC_PARAM>
     *                                     <NUMBER_OF_PACKETS>567250</NUMBER_OF_PACKETS>
     *                                     <LATENCY>0</LATENCY>
     *                                     <PACKET_LOSS>0</PACKET_LOSS>
     *                                     <JITTER>0</JITTER>
     *                                     <JITTER_PEAK>0</JITTER_PEAK>
     *                                     <FRACTION_LOSS>0</FRACTION_LOSS>
     *                                     <FRACTION_LOSS_PEAK>0</FRACTION_LOSS_PEAK>
     *                                 </GLOBAL_PARAM>
     *                                 <FRAME_PER_PACKET>1</FRAME_PER_PACKET>
     *                             </ADVANCED_AUDIO_PARAM>
     *                             <RTP_INFO>
     *                                 <RTP_PACKET_LOSS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </RTP_PACKET_LOSS>
     *                                 <RTP_OUT_OF_ORDER>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </RTP_OUT_OF_ORDER>
     *                                 <RTP_FRAGMENT_PACKETS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </RTP_FRAGMENT_PACKETS>
     *                                 <JITTER_BUFFER_SIZE>
     *                                     <ACCUMULATE>-1</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_BUFFER_SIZE>
     *                                 <JITTER_LATE_PACKETS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_LATE_PACKETS>
     *                                 <JITTER_OVERFLOWS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_OVERFLOWS>
     *                                 <JITTER_SAMPLE_PACKET_INTERVAL>
     *                                     <ACCUMULATE>-1</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_SAMPLE_PACKET_INTERVAL>
     *                             </RTP_INFO>
     *                         </ADVANCED_AUDIO_IN>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <ADVANCED_AUDIO_PARAM>
     *                             <GLOBAL_PARAM>
     *                                 <BASIC_PARAM>
     *                                     <MAP_PROBLEM/>
     *                                     <BIT_RATE>48000</BIT_RATE>
     *                                     <PROTOCOL>g7221C_48k</PROTOCOL>
     *                                     <CHANNEL_INDEX>1</CHANNEL_INDEX>
     *                                     <CHANNEL_TYPE>audio_out</CHANNEL_TYPE>
     *                                     <PARTY_ADDRESS>172.16.100.66</PARTY_ADDRESS>
     *                                     <MCU_ADDRESS>172.16.100.70</MCU_ADDRESS>
     *                                     <PARTY_PORT>50068</PARTY_PORT>
     *                                     <MCU_PORT>49390</MCU_PORT>
     *                                     <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                                     <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                                     <CONNECTION_STATUS>1</CONNECTION_STATUS>
     *                                     <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                                     <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                                     <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                                     <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                                     <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                                     <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                                     <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                                 </BASIC_PARAM>
     *                                 <NUMBER_OF_PACKETS>567260</NUMBER_OF_PACKETS>
     *                                 <LATENCY>0</LATENCY>
     *                                 <PACKET_LOSS>0</PACKET_LOSS>
     *                                 <JITTER>1</JITTER>
     *                                 <JITTER_PEAK>4</JITTER_PEAK>
     *                                 <FRACTION_LOSS>0</FRACTION_LOSS>
     *                                 <FRACTION_LOSS_PEAK>0</FRACTION_LOSS_PEAK>
     *                             </GLOBAL_PARAM>
     *                             <FRAME_PER_PACKET>1</FRAME_PER_PACKET>
     *                         </ADVANCED_AUDIO_PARAM>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <ADVANCED_VIDEO_IN>
     *                             <ADVANCED_VIDEO_PARAM>
     *                                 <GLOBAL_PARAM>
     *                                     <BASIC_PARAM>
     *                                         <MAP_PROBLEM/>
     *                                         <BIT_RATE>760779</BIT_RATE>
     *                                         <PROTOCOL>h264</PROTOCOL>
     *                                         <CHANNEL_INDEX>1</CHANNEL_INDEX>
     *                                         <CHANNEL_TYPE>video_in</CHANNEL_TYPE>
     *                                         <PARTY_ADDRESS>172.16.100.66</PARTY_ADDRESS>
     *                                         <MCU_ADDRESS>172.16.100.70</MCU_ADDRESS>
     *                                         <PARTY_PORT>50070</PARTY_PORT>
     *                                         <MCU_PORT>49392</MCU_PORT>
     *                                         <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                                         <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                                         <CONNECTION_STATUS>1</CONNECTION_STATUS>
     *                                         <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                                         <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                                         <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                                         <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                                         <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                                         <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                                         <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                                     </BASIC_PARAM>
     *                                     <NUMBER_OF_PACKETS>1011979</NUMBER_OF_PACKETS>
     *                                     <LATENCY>0</LATENCY>
     *                                     <PACKET_LOSS>0</PACKET_LOSS>
     *                                     <JITTER>0</JITTER>
     *                                     <JITTER_PEAK>0</JITTER_PEAK>
     *                                     <FRACTION_LOSS>0</FRACTION_LOSS>
     *                                     <FRACTION_LOSS_PEAK>0</FRACTION_LOSS_PEAK>
     *                                 </GLOBAL_PARAM>
     *                                 <ANNEXES/>
     *                                 <RESOLUTION>720p</RESOLUTION>
     *                                 <VIDEO_FRAME_RATE>20</VIDEO_FRAME_RATE>
     *                                 <RESOLUTION_WIDTH>1280</RESOLUTION_WIDTH>
     *                                 <RESOLUTION_HEIGHT>720</RESOLUTION_HEIGHT>
     *                             </ADVANCED_VIDEO_PARAM>
     *                             <RTP_INFO>
     *                                 <RTP_PACKET_LOSS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </RTP_PACKET_LOSS>
     *                                 <RTP_OUT_OF_ORDER>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </RTP_OUT_OF_ORDER>
     *                                 <RTP_FRAGMENT_PACKETS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </RTP_FRAGMENT_PACKETS>
     *                                 <JITTER_BUFFER_SIZE>
     *                                     <ACCUMULATE>-1</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_BUFFER_SIZE>
     *                                 <JITTER_LATE_PACKETS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_LATE_PACKETS>
     *                                 <JITTER_OVERFLOWS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_OVERFLOWS>
     *                                 <JITTER_SAMPLE_PACKET_INTERVAL>
     *                                     <ACCUMULATE>-1</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_SAMPLE_PACKET_INTERVAL>
     *                             </RTP_INFO>
     *                             <ERROR_RESILIENCE>
     *                                 <ACCUMULATE>0</ACCUMULATE>
     *                                 <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                 <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                 <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                 <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                             </ERROR_RESILIENCE>
     *                         </ADVANCED_VIDEO_IN>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <ADVANCED_VIDEO_PARAM>
     *                             <GLOBAL_PARAM>
     *                                 <BASIC_PARAM>
     *                                     <MAP_PROBLEM/>
     *                                     <BIT_RATE>774841</BIT_RATE>
     *                                     <PROTOCOL>h264</PROTOCOL>
     *                                     <CHANNEL_INDEX>1</CHANNEL_INDEX>
     *                                     <CHANNEL_TYPE>video_out</CHANNEL_TYPE>
     *                                     <PARTY_ADDRESS>172.16.100.66</PARTY_ADDRESS>
     *                                     <MCU_ADDRESS>172.16.100.70</MCU_ADDRESS>
     *                                     <PARTY_PORT>50070</PARTY_PORT>
     *                                     <MCU_PORT>49392</MCU_PORT>
     *                                     <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                                     <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                                     <CONNECTION_STATUS>1</CONNECTION_STATUS>
     *                                     <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                                     <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                                     <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                                     <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                                     <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                                     <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                                     <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                                 </BASIC_PARAM>
     *                                 <NUMBER_OF_PACKETS>1130756</NUMBER_OF_PACKETS>
     *                                 <LATENCY>0</LATENCY>
     *                                 <PACKET_LOSS>0</PACKET_LOSS>
     *                                 <JITTER>6</JITTER>
     *                                 <JITTER_PEAK>74</JITTER_PEAK>
     *                                 <FRACTION_LOSS>0</FRACTION_LOSS>
     *                                 <FRACTION_LOSS_PEAK>0</FRACTION_LOSS_PEAK>
     *                             </GLOBAL_PARAM>
     *                             <ANNEXES/>
     *                             <RESOLUTION>720p</RESOLUTION>
     *                             <VIDEO_FRAME_RATE>25</VIDEO_FRAME_RATE>
     *                             <RESOLUTION_WIDTH>1280</RESOLUTION_WIDTH>
     *                             <RESOLUTION_HEIGHT>720</RESOLUTION_HEIGHT>
     *                         </ADVANCED_VIDEO_PARAM>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <ADVANCED_VIDEO_IN>
     *                             <ADVANCED_VIDEO_PARAM>
     *                                 <GLOBAL_PARAM>
     *                                     <BASIC_PARAM>
     *                                         <MAP_PROBLEM/>
     *                                         <BIT_RATE>0</BIT_RATE>
     *                                         <PROTOCOL>h264</PROTOCOL>
     *                                         <CHANNEL_INDEX>1</CHANNEL_INDEX>
     *                                         <CHANNEL_TYPE>video_content_in</CHANNEL_TYPE>
     *                                         <PARTY_ADDRESS>172.16.100.66</PARTY_ADDRESS>
     *                                         <MCU_ADDRESS>172.16.100.70</MCU_ADDRESS>
     *                                         <PARTY_PORT>50074</PARTY_PORT>
     *                                         <MCU_PORT>49396</MCU_PORT>
     *                                         <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                                         <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                                         <CONNECTION_STATUS>1</CONNECTION_STATUS>
     *                                         <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                                         <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                                         <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                                         <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                                         <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                                         <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                                         <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                                     </BASIC_PARAM>
     *                                     <NUMBER_OF_PACKETS>0</NUMBER_OF_PACKETS>
     *                                     <LATENCY>0</LATENCY>
     *                                     <PACKET_LOSS>0</PACKET_LOSS>
     *                                     <JITTER>0</JITTER>
     *                                     <JITTER_PEAK>0</JITTER_PEAK>
     *                                     <FRACTION_LOSS>0</FRACTION_LOSS>
     *                                     <FRACTION_LOSS_PEAK>0</FRACTION_LOSS_PEAK>
     *                                 </GLOBAL_PARAM>
     *                                 <ANNEXES/>
     *                                 <RESOLUTION>unknown</RESOLUTION>
     *                                 <VIDEO_FRAME_RATE>0</VIDEO_FRAME_RATE>
     *                                 <RESOLUTION_WIDTH>0</RESOLUTION_WIDTH>
     *                                 <RESOLUTION_HEIGHT>0</RESOLUTION_HEIGHT>
     *                             </ADVANCED_VIDEO_PARAM>
     *                             <RTP_INFO>
     *                                 <RTP_PACKET_LOSS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </RTP_PACKET_LOSS>
     *                                 <RTP_OUT_OF_ORDER>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </RTP_OUT_OF_ORDER>
     *                                 <RTP_FRAGMENT_PACKETS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </RTP_FRAGMENT_PACKETS>
     *                                 <JITTER_BUFFER_SIZE>
     *                                     <ACCUMULATE>-1</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_BUFFER_SIZE>
     *                                 <JITTER_LATE_PACKETS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_LATE_PACKETS>
     *                                 <JITTER_OVERFLOWS>
     *                                     <ACCUMULATE>0</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_OVERFLOWS>
     *                                 <JITTER_SAMPLE_PACKET_INTERVAL>
     *                                     <ACCUMULATE>-1</ACCUMULATE>
     *                                     <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                     <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                     <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                     <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                                 </JITTER_SAMPLE_PACKET_INTERVAL>
     *                             </RTP_INFO>
     *                             <ERROR_RESILIENCE>
     *                                 <ACCUMULATE>0</ACCUMULATE>
     *                                 <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                 <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                 <INTERVAL_NUMBER>0</INTERVAL_NUMBER>
     *                                 <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                             </ERROR_RESILIENCE>
     *                         </ADVANCED_VIDEO_IN>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <ADVANCED_VIDEO_PARAM>
     *                             <GLOBAL_PARAM>
     *                                 <BASIC_PARAM>
     *                                     <MAP_PROBLEM/>
     *                                     <BIT_RATE>0</BIT_RATE>
     *                                     <PROTOCOL>h264</PROTOCOL>
     *                                     <CHANNEL_INDEX>1</CHANNEL_INDEX>
     *                                     <CHANNEL_TYPE>video_content_out</CHANNEL_TYPE>
     *                                     <PARTY_ADDRESS>172.16.100.66</PARTY_ADDRESS>
     *                                     <MCU_ADDRESS>172.16.100.70</MCU_ADDRESS>
     *                                     <PARTY_PORT>50074</PARTY_PORT>
     *                                     <MCU_PORT>49396</MCU_PORT>
     *                                     <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                                     <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                                     <CONNECTION_STATUS>1</CONNECTION_STATUS>
     *                                     <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                                     <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                                     <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                                     <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                                     <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                                     <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                                     <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                                 </BASIC_PARAM>
     *                                 <NUMBER_OF_PACKETS>0</NUMBER_OF_PACKETS>
     *                                 <LATENCY>0</LATENCY>
     *                                 <PACKET_LOSS>0</PACKET_LOSS>
     *                                 <JITTER>0</JITTER>
     *                                 <JITTER_PEAK>0</JITTER_PEAK>
     *                                 <FRACTION_LOSS>0</FRACTION_LOSS>
     *                                 <FRACTION_LOSS_PEAK>0</FRACTION_LOSS_PEAK>
     *                             </GLOBAL_PARAM>
     *                             <ANNEXES/>
     *                             <RESOLUTION>unknown</RESOLUTION>
     *                             <VIDEO_FRAME_RATE>0</VIDEO_FRAME_RATE>
     *                             <RESOLUTION_WIDTH>0</RESOLUTION_WIDTH>
     *                             <RESOLUTION_HEIGHT>0</RESOLUTION_HEIGHT>
     *                         </ADVANCED_VIDEO_PARAM>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <ADVANCED_FECC_IN>
     *                             <GLOBAL_PARAM>
     *                                 <BASIC_PARAM>
     *                                     <MAP_PROBLEM/>
     *                                     <BIT_RATE>-1</BIT_RATE>
     *                                     <PROTOCOL>g711Alaw64k</PROTOCOL>
     *                                     <CHANNEL_INDEX>0</CHANNEL_INDEX>
     *                                     <CHANNEL_TYPE>fecc_in</CHANNEL_TYPE>
     *                                     <PARTY_ADDRESS>0.0.0.0</PARTY_ADDRESS>
     *                                     <MCU_ADDRESS>0.0.0.0</MCU_ADDRESS>
     *                                     <PARTY_PORT>0</PARTY_PORT>
     *                                     <MCU_PORT>0</MCU_PORT>
     *                                     <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                                     <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                                     <CONNECTION_STATUS>0</CONNECTION_STATUS>
     *                                     <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                                     <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                                     <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                                     <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                                     <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                                     <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                                     <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                                 </BASIC_PARAM>
     *                                 <NUMBER_OF_PACKETS>0</NUMBER_OF_PACKETS>
     *                                 <LATENCY>0</LATENCY>
     *                                 <PACKET_LOSS>0</PACKET_LOSS>
     *                                 <JITTER>0</JITTER>
     *                                 <JITTER_PEAK>0</JITTER_PEAK>
     *                                 <FRACTION_LOSS>0</FRACTION_LOSS>
     *                                 <FRACTION_LOSS_PEAK>0</FRACTION_LOSS_PEAK>
     *                             </GLOBAL_PARAM>
     *                             <RTP_PACKET_LOSS>
     *                                 <ACCUMULATE>0</ACCUMULATE>
     *                                 <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                 <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                 <INTERVAL_NUMBER>65535</INTERVAL_NUMBER>
     *                                 <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                             </RTP_PACKET_LOSS>
     *                             <RTP_OUT_OF_ORDER>
     *                                 <ACCUMULATE>0</ACCUMULATE>
     *                                 <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                 <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                 <INTERVAL_NUMBER>65535</INTERVAL_NUMBER>
     *                                 <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                             </RTP_OUT_OF_ORDER>
     *                             <RTP_FRAGMENT_PACKETS>
     *                                 <ACCUMULATE>0</ACCUMULATE>
     *                                 <ACCUMULATE_PERCENT>-1</ACCUMULATE_PERCENT>
     *                                 <INTERVAL_PERCENT>-1</INTERVAL_PERCENT>
     *                                 <INTERVAL_NUMBER>65535</INTERVAL_NUMBER>
     *                                 <INTERVAL_PEAK>0</INTERVAL_PEAK>
     *                             </RTP_FRAGMENT_PACKETS>
     *                         </ADVANCED_FECC_IN>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <GLOBAL_PARAM>
     *                             <BASIC_PARAM>
     *                                 <MAP_PROBLEM/>
     *                                 <BIT_RATE>-1</BIT_RATE>
     *                                 <PROTOCOL>g711Alaw64k</PROTOCOL>
     *                                 <CHANNEL_INDEX>0</CHANNEL_INDEX>
     *                                 <CHANNEL_TYPE>fecc_out</CHANNEL_TYPE>
     *                                 <PARTY_ADDRESS>0.0.0.0</PARTY_ADDRESS>
     *                                 <MCU_ADDRESS>0.0.0.0</MCU_ADDRESS>
     *                                 <PARTY_PORT>0</PARTY_PORT>
     *                                 <MCU_PORT>0</MCU_PORT>
     *                                 <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                                 <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                                 <CONNECTION_STATUS>0</CONNECTION_STATUS>
     *                                 <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                                 <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                                 <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                                 <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                                 <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                                 <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                                 <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                             </BASIC_PARAM>
     *                             <NUMBER_OF_PACKETS>0</NUMBER_OF_PACKETS>
     *                             <LATENCY>0</LATENCY>
     *                             <PACKET_LOSS>0</PACKET_LOSS>
     *                             <JITTER>0</JITTER>
     *                             <JITTER_PEAK>0</JITTER_PEAK>
     *                             <FRACTION_LOSS>0</FRACTION_LOSS>
     *                             <FRACTION_LOSS_PEAK>0</FRACTION_LOSS_PEAK>
     *                         </GLOBAL_PARAM>
     *                     </IP_MONITOR_CHANNEL>
     *                     <IP_MONITOR_CHANNEL>
     *                         <BASIC_PARAM>
     *                             <MAP_PROBLEM/>
     *                             <BIT_RATE>-1</BIT_RATE>
     *                             <PROTOCOL>UnknownAlgorithm</PROTOCOL>
     *                             <CHANNEL_INDEX>0</CHANNEL_INDEX>
     *                             <CHANNEL_TYPE>bfcp_udp</CHANNEL_TYPE>
     *                             <PARTY_ADDRESS>172.16.100.66</PARTY_ADDRESS>
     *                             <MCU_ADDRESS>172.16.100.70</MCU_ADDRESS>
     *                             <PARTY_PORT>49398</PARTY_PORT>
     *                             <MCU_PORT>49398</MCU_PORT>
     *                             <PARTY_TRANSPORT_TYPE>0</PARTY_TRANSPORT_TYPE>
     *                             <MCU_TRANSPORT_TYPE>0</MCU_TRANSPORT_TYPE>
     *                             <CONNECTION_STATUS>1</CONNECTION_STATUS>
     *                             <ICE_PARTY_ADDRESS>0.0.0.0</ICE_PARTY_ADDRESS>
     *                             <ICE_MCU_ADDRESS>0.0.0.0</ICE_MCU_ADDRESS>
     *                             <ICE_PARTY_PORT>0</ICE_PARTY_PORT>
     *                             <ICE_MCU_PORT>0</ICE_MCU_PORT>
     *                             <ICE_PARTY_TRANSPORT_TYPE>0</ICE_PARTY_TRANSPORT_TYPE>
     *                             <ICE_MCU_TRANSPORT_TYPE>0</ICE_MCU_TRANSPORT_TYPE>
     *                             <ICE_CONNECTION_TYPE>none</ICE_CONNECTION_TYPE>
     *                         </BASIC_PARAM>
     *                     </IP_MONITOR_CHANNEL>
     *                 </IP_MONITOR_CHANNELS>
     *             </ONGOING_PARTY>
     *         </GET>
     *     </ACTION>
     * </RESPONSE_TRANS_PARTY>
     *
     * @param xml
     */

    public CcGetTerminalInfoResponse(String xml) {

        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_PARTY = jsonObject.getJSONObject("RESPONSE_TRANS_PARTY");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_PARTY.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject action = RESPONSE_TRANS_PARTY.getJSONObject("ACTION");
                    JSONObject get = action.getJSONObject("GET");
                    JSONObject ongoing_party = get.getJSONObject("ONGOING_PARTY");
                    JSONObject party = ongoing_party.getJSONObject("PARTY");
                    id = String.valueOf(party.get("ID"));
                    JSONObject ip_monitor_channels = ongoing_party.getJSONObject("IP_MONITOR_CHANNELS");
                    JSONArray ip_monitor_channel = ip_monitor_channels.getJSONArray("IP_MONITOR_CHANNEL");
                    for (Object ip_monitor_channel_obj : ip_monitor_channel) {
                        JSONObject ip_monitor_channel_object = (JSONObject) ip_monitor_channel_obj;

                        // ADVANCED_AUDIO_IN
                        if (ip_monitor_channel_object.has("ADVANCED_AUDIO_IN")) {
                            JSONObject advanced_audio_in = ip_monitor_channel_object.getJSONObject("ADVANCED_AUDIO_IN");
                            if (advanced_audio_in != null) {
                                JSONObject advanced_audio_param = advanced_audio_in.getJSONObject("ADVANCED_AUDIO_PARAM");
                                JSONObject global_param = advanced_audio_param.getJSONObject("GLOBAL_PARAM");
                                int number_of_packets = global_param.getInt("NUMBER_OF_PACKETS");
                                int packet_loss = global_param.getInt("PACKET_LOSS");
                                JSONObject basic_param = global_param.getJSONObject("BASIC_PARAM");
                                audio_in_bit_rate = basic_param.getInt("BIT_RATE") / 1000;
                                audio_in_protocol = basic_param.getString("PROTOCOL");
                                if (number_of_packets > 0) {
                                    audio_in_packet_loss = packet_loss * 100 / number_of_packets;
                                } else {
                                    audio_in_packet_loss = 0;
                                }
                                audio_in_jitter = global_param.getInt("JITTER");
                            }
                        }

                        // ADVANCED_AUDIO_PARAM
                        if (ip_monitor_channel_object.has("ADVANCED_AUDIO_PARAM")) {
                            JSONObject advanced_audio_param = ip_monitor_channel_object.getJSONObject("ADVANCED_AUDIO_PARAM");
                            if (advanced_audio_param != null) {
                                JSONObject global_param = advanced_audio_param.getJSONObject("GLOBAL_PARAM");
                                JSONObject basic_param = global_param.getJSONObject("BASIC_PARAM");
                                String channel_type = basic_param.getString("CHANNEL_TYPE");
                                if (StringUtils.isNotEmpty(channel_type) && channel_type.equals("audio_out")) {
                                    audio_out_bit_rate = basic_param.getInt("BIT_RATE") / 1000;
                                    audio_out_protocol = basic_param.getString("PROTOCOL");
                                }
                                int number_of_packets = global_param.getInt("NUMBER_OF_PACKETS");
                                int packet_loss = global_param.getInt("PACKET_LOSS");
                                if (number_of_packets > 0) {
                                    audio_out_packet_loss = (packet_loss * 100) / number_of_packets;
                                } else {
                                    audio_out_packet_loss = 0;
                                }
                                audio_out_jitter = global_param.getInt("JITTER");
                            }
                        }

                        // ADVANCED_VIDEO_IN
                        if (ip_monitor_channel_object.has("ADVANCED_VIDEO_IN")) {
                            JSONObject advanced_video_in = ip_monitor_channel_object.getJSONObject("ADVANCED_VIDEO_IN");
                            if (advanced_video_in != null) {
                                JSONObject advanced_video_param = advanced_video_in.getJSONObject("ADVANCED_VIDEO_PARAM");
                                JSONObject global_param = advanced_video_param.getJSONObject("GLOBAL_PARAM");
                                JSONObject basic_param = global_param.getJSONObject("BASIC_PARAM");
                                String channel_type = basic_param.getString("CHANNEL_TYPE");
                                if (channel_type.equals("video_in")) {
                                    video_in_bit_rate = basic_param.getInt("BIT_RATE") / 1000;
                                    video_in_protocol = basic_param.getString("PROTOCOL");
                                    int number_of_packets = global_param.getInt("NUMBER_OF_PACKETS");
                                    int packet_loss = global_param.getInt("PACKET_LOSS");
                                    if (number_of_packets > 0) {
                                        video_in_packet_loss = (packet_loss * 100) / number_of_packets;
                                    } else {
                                        video_in_packet_loss = 0;
                                    }
                                    video_in_video_frame_rate = advanced_video_param.getInt("VIDEO_FRAME_RATE");
                                    String resolution_width = String.valueOf(advanced_video_param.get("RESOLUTION_WIDTH"));
                                    String resolution_height = String.valueOf(advanced_video_param.get("RESOLUTION_HEIGHT"));
                                    video_in_resolution = resolution_width + "x" + resolution_height;
                                    video_in_jitter = global_param.getInt("JITTER");
                                } else if (channel_type.equals("video_content_in")) {
                                    video_content_in_bit_rate = basic_param.getInt("BIT_RATE") / 1000;
                                    video_content_in_protocol = basic_param.getString("PROTOCOL");
                                    int number_of_packets = global_param.getInt("NUMBER_OF_PACKETS");
                                    int packet_loss = global_param.getInt("PACKET_LOSS");
                                    if (number_of_packets > 0) {
                                        video_content_in_packet_loss = (packet_loss * 100) / number_of_packets;
                                    } else {
                                        video_content_in_packet_loss = 0;
                                    }
                                    video_content_in_video_frame_rate = advanced_video_param.getInt("VIDEO_FRAME_RATE");
                                    String resolution_width = String.valueOf(advanced_video_param.get("RESOLUTION_WIDTH"));
                                    String resolution_height = String.valueOf(advanced_video_param.get("RESOLUTION_HEIGHT"));
                                    video_content_in_resolution = resolution_width + "x" + resolution_height;
                                    video_content_in_jitter = global_param.getInt("JITTER");
                                    if (video_content_in_bit_rate == 0) {
                                        video_content_in_jitter = null;
                                    }
                                }
                            }
                        }

                        // ADVANCED_VIDEO_PARAM
                        if (ip_monitor_channel_object.has("ADVANCED_VIDEO_PARAM")) {
                            JSONObject advanced_video_param = ip_monitor_channel_object.getJSONObject("ADVANCED_VIDEO_PARAM");
                            if (advanced_video_param != null) {
                                JSONObject global_param = advanced_video_param.getJSONObject("GLOBAL_PARAM");
                                JSONObject basic_param = global_param.getJSONObject("BASIC_PARAM");
                                String channel_type = basic_param.getString("CHANNEL_TYPE");
                                if (channel_type.equals("video_out")) {
                                    video_out_bit_rate = basic_param.getInt("BIT_RATE") / 1000;
                                    video_out_protocol = basic_param.getString("PROTOCOL");
                                    int number_of_packets = global_param.getInt("NUMBER_OF_PACKETS");
                                    int packet_loss = global_param.getInt("PACKET_LOSS");
                                    if (number_of_packets > 0) {
                                        video_out_packet_loss = (packet_loss * 100) /number_of_packets;
                                    } else {
                                        video_out_packet_loss = 0;
                                    }
                                    video_out_video_frame_rate = advanced_video_param.getInt("VIDEO_FRAME_RATE");
                                    String resolution_width = String.valueOf(advanced_video_param.get("RESOLUTION_WIDTH"));
                                    String resolution_height = String.valueOf(advanced_video_param.get("RESOLUTION_HEIGHT"));
                                    video_out_resolution = resolution_width + "x" + resolution_height;
                                    video_out_jitter = global_param.getInt("JITTER");
                                } else if (channel_type.equals("video_content_out")) {
                                    video_content_out_bit_rate = basic_param.getInt("BIT_RATE") / 1000;
                                    video_content_out_protocol = basic_param.getString("PROTOCOL");
                                    int number_of_packets = global_param.getInt("NUMBER_OF_PACKETS");
                                    int packet_loss = global_param.getInt("PACKET_LOSS");
                                    if (number_of_packets > 0) {
                                        video_content_out_packet_loss = (packet_loss * 100) / number_of_packets;
                                    } else {
                                        video_content_out_packet_loss = 0;
                                    }
                                    video_content_out_video_frame_rate = advanced_video_param.getInt("VIDEO_FRAME_RATE");
                                    String resolution_width = String.valueOf(advanced_video_param.get("RESOLUTION_WIDTH"));
                                    String resolution_height = String.valueOf(advanced_video_param.get("RESOLUTION_HEIGHT"));
                                    video_content_out_resolution = resolution_width + "x" + resolution_height;
                                    video_content_out_jitter = global_param.getInt("JITTER");
                                    if (video_content_out_bit_rate == 0) {
                                        video_content_out_jitter = null;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
