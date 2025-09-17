package com.paradisecloud.fcm.web.task;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.web.cache.LiveBroadcastCache;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * @author admin
 */
public class QwenActionCheckTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(QwenActionCheckTask.class);

    private String conferenceId;
    private String attendeeId;

    private String apiKey = "sk-3828ed258cd545868aa548a9dc1d4945";
    private String model = "qwen2.5-vl-7b-instruct";
    private String message = "" +
            "# Role: 行为检测助手\n" +
            "\n" +
            "## Profile\n" +
            "- language: 中文\n" +
            "- description: 一位专业的行为检测助手、人类识别助手，能够识别并响应特定的行为模式和识别图中有几个人类。\n" +
            "- background: 在机器视觉和行为识别领域接受过培训，具备处理多种行为监测的能力。\n" +
            "- personality: 细致、敏锐、快速响应。\n" +
            "- expertise: 行为识别、图像处理、数据分析。\n" +
            "- target_audience: 会议行为监测识别。\n" +
            "\n" +
            "## Skills\n" +
            "\n" +
            "1. 行为识别\n" +
            "   - 图像分析: 能够快速分析图像数据，识别特定行为。\n" +
            "   - 模式识别: 精确判断和分类不同类型的行为。\n" +
            "   - 事件触发: 能根据识别到的行为自动触发相应的输出。\n" +
            "   - 数据输出: 生成标准化的输出格式，便于数据整合与分析。\n" +
            "\n" +
            "2. 数据处理\n" +
            "   - JSON格式化: 将识别结果转换为标准的JSON输出。\n" +
            "   - 数据验证: 确保输出结果的准确性与完整性。\n" +
            "   - 性能评估: 不断评估识别算法的性能，并优化识别精度。\n" +
            "\n" +
            "## Rules\n" +
            "\n" +
            "1. 基本原则：\n" +
            "   - 准确性: 确保行为检测的准确性，不漏报、不误报。\n" +
            "   - 实时性: 尽量在最短时间内完成行为识别和输出。\n" +
            "   - 规范性: 输出结果必须符合预定的JSON格式。\n" +
            "   - 保密性: 不保留任何用户敏感信息，保护隐私。\n" +
            "   - 行为标签：抽烟、打瞌睡、走动、玩手机、无人、私下交谈、吃东西\n" +
            "\n" +
            "2. 行为准则：\n" +
            "   - 行为判断: 根据预设标准明确判断是否存在特定行为。\n" +
            "   - 反馈机制: 针对不同检测结果，提供标准化反馈。\n" +
            "   - 更新数据: 定期更新行为检测标准和算法。\n" +
            "   - 适应性: 能根据环境变化调整检测策略。\n" +
            "\n" +
            "3. 限制条件：\n" +
            "   - 数据范围: 所有检测只能基于已提供的图像数据。\n" +
            "   - 环境限制: 检测在特定环境下进行，避免干扰因素影响结果。\n" +
            "   - 技术限制: 必须使用标准的识别技术，不可使用未经验证的方法。\n" +
            "   - 输出限制：只输出json结果，不要输出其它任何内容。输出的json结果中名称必须跟行为标签完全一致。\n" +
            "\n" +
            "## Workflows\n" +
            "\n" +
            "- 目标: 精确识别图像中的特定行为并输出JSON格式的结果。\n" +
            "- 步骤 1: 接收并分析输入的图像数据，以识别是否存在指定行为。\n" +
            "- 步骤 2: 对识别出的行为进行分类，记录存在与否。\n" +
            "- 步骤 3: 根据识别结果生成符合标准的JSON格式输出。\n" +
            "- 预期结果: 输出一个包含图中人数、行为识别结果的JSON，未检测到的行为标签不用罗列，行为标签的值如果为false，也不用罗列;必须保证输出的结果在\"抽烟、打瞌睡、走动、玩手机、无人、私下交谈、吃东西\"标签清单中，不得发散不得输出无关内容。例如：会场中有3人且有人抽烟时返回{\"人数\":\"3\",\"抽烟\":\"true\"}、会场中有10人且无行为标签中行为时返回{\"人数\":\"10\"}。\n" +
            "\n" +
            "## Initialization\n" +
            "作为行为检测助手，你必须遵守上述Rules，按照Workflows执行任务。";

    public QwenActionCheckTask(String id, long delayInMilliseconds, String conferenceId, String attendeeId) {
        super("qwen_action_check_" + id, delayInMilliseconds);
        this.conferenceId = conferenceId;
        this.attendeeId = attendeeId;
    }


    @Override
    public void run() {
        LOGGER.info("千文行为识别 ID:" + getId());
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getByConferenceId(conferenceId);
        if (conferenceContext == null) {
            LOGGER.info("无此会议！");
            return;
        }
        JSONObject params = new JSONObject();
        params.put("direction", "rx");
        params.put("maxWidth", "1280");
        String snapshot = BeanFactory.getBean(IAttendeeService.class).takeSnapshot(conferenceId, attendeeId, params);
        if (StringUtils.isEmpty(snapshot)) {
            LOGGER.info("无预览图像！");
            return;
        }
        String dashscopeApiKey = ExternalConfigCache.getInstance().getDashscopeApiKey();
        if (StringUtils.isNotEmpty(dashscopeApiKey)) {
            apiKey = dashscopeApiKey;
        }
        String dashscopeModel = ExternalConfigCache.getInstance().getDashscopeModel();
        if (StringUtils.isNotEmpty(dashscopeModel)) {
            model = dashscopeModel;
        }
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", "data:image/png;base64," + snapshot),
                        Collections.singletonMap("text", message))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(apiKey)
                // 此处以qwen-vl-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model(model)
                .message(userMessage)
                .build();
        MultiModalConversationResult result = null;
        try {
            result = conv.call(param);
            LOGGER.info("### 返回结果" + JsonUtils.toJson(result));
            try {
                String text = (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
                LOGGER.info("### 返回结果 json:" + text);
                String json = text.substring(text.indexOf("{"), text.indexOf("}") + 1);
                ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                Map<String, Object> map = objectMapper.readValue(json, Map.class);
                boolean captured = false;
                Map<String, Object> mapCaptured = new HashMap<>();
                for (String key : map.keySet()) {
                    Object value = map.get(key);
                    if (value == Boolean.TRUE) {
                        captured = true;
                        mapCaptured.put(key, value);
                    }
                    if ("人数".equals(key)) {
                        mapCaptured.put(key, value);
                    }
                }
                if (captured) {
                    Long historyConferenceId = conferenceContext.getHistoryConference().getId();
                    String coSpace = conferenceContext.getHistoryConference().getCoSpace();
                    String path = getSavePath();
                    File folder = new File(path, coSpace);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    String imageName = "motion_" + System.currentTimeMillis() + ".png";
                    File file = new File(folder.getAbsolutePath(), imageName);
                    byte[] imageBytes = Base64.getDecoder().decode(snapshot);
                    FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsoluteFile());
                    fileOutputStream.write(imageBytes);
                    fileOutputStream.close();
                    BusiConferenceMotionCapture busiConferenceMotionCapture = new BusiConferenceMotionCapture();
                    busiConferenceMotionCapture.setHistoryConferenceId(historyConferenceId);
                    busiConferenceMotionCapture.setCoSpace(coSpace);
                    busiConferenceMotionCapture.setMotion(objectMapper.writeValueAsString(mapCaptured));
                    busiConferenceMotionCapture.setImageName(imageName);
                    busiConferenceMotionCapture.setCreateTime(new Date());
                    BeanFactory.getBean(BusiConferenceMotionCaptureMapper.class).insertBusiConferenceMotionCapture(busiConferenceMotionCapture);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NoApiKeyException e) {
            e.printStackTrace();
        } catch (UploadFileException e) {
            e.printStackTrace();
        }
    }

    private static String getSavePath() {
        String os = System.getProperty("os.name");
        if (os.contains("indows")) {
            return PathUtil.getRootPath();
        } else {
            return "/mnt/nfs/spaces";
        }
    }
}
