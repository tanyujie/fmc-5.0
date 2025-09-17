import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

import com.sinhy.utils.IOUtils;
import com.sinhy.utils.IOUtils.LineStrProcessor;

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CleanDBSqlGenerator.java
 * Package     : 
 * @author sinhy 
 * @since 2021-10-21 14:27
 * @version  V1.0
 */

/**  
 * <pre>数据库清理sql脚本生成器</pre>
 * @author sinhy
 * @since 2021-10-21 14:27
 * @version V1.0  
 */
public class CleanDBSqlGenerator
{
    
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        String tables = "busi_call_leg_profile\r\n"
                + "busi_conference\r\n"
                + "busi_conference_appointment\r\n"
                + "busi_conference_number\r\n"
                + "busi_conference_number_section\r\n"
                + "busi_edu_class\r\n"
                + "busi_edu_classroom\r\n"
                + "busi_edu_learning_stage\r\n"
                + "busi_edu_school_building\r\n"
                + "busi_edu_section_item\r\n"
                + "busi_edu_section_scheme\r\n"
                + "busi_edu_section_strategy\r\n"
                + "busi_edu_subject\r\n"
                + "busi_fme\r\n"
                + "busi_fme_cluster\r\n"
                + "busi_fme_cluster_map\r\n"
                + "busi_fme_dept\r\n"
                + "busi_free_switch\r\n"
                + "busi_free_switch_dept\r\n"
                + "busi_fsbc_registration_server\r\n"
                + "busi_fsbc_server_dept\r\n"
                + "busi_history_call\r\n"
                + "busi_history_conference\r\n"
                + "busi_history_participant\r\n"
                + "busi_live_setting\r\n"
                + "busi_mqtt\r\n"
                + "busi_mqtt_cluster\r\n"
                + "busi_mqtt_cluster_map\r\n"
                + "busi_mqtt_dept\r\n"
                + "busi_profile_call\r\n"
                + "busi_profile_call_branding\r\n"
                + "busi_profile_compatibility\r\n"
                + "busi_profile_dial_in_security\r\n"
                + "busi_profile_dtmf\r\n"
                + "busi_profile_ivr_branding\r\n"
                + "busi_record_setting\r\n"
                + "busi_records\r\n"
                + "busi_sip_account\r\n"
                + "busi_template_conference\r\n"
                + "busi_template_conference_default_view_cell_screen\r\n"
                + "busi_template_conference_default_view_dept\r\n"
                + "busi_template_conference_default_view_paticipant\r\n"
                + "busi_template_dept\r\n"
                + "busi_template_participant\r\n"
                + "busi_template_polling_dept\r\n"
                + "busi_template_polling_paticipant\r\n"
                + "busi_template_polling_scheme\r\n"
                + "busi_tenant_settings\r\n"
                + "busi_terminal\r\n"
                + "busi_terminal_action\r\n"
                + "busi_terminal_log\r\n"
                + "busi_terminal_meeting_join_settings\r\n"
                + "busi_terminal_sys_info\r\n"
                + "busi_terminal_upgrade\r\n"
                + "busi_token\r\n"
                + "cdr_call\r\n"
                + "cdr_call_leg_end\r\n"
                + "cdr_call_leg_end_alarm\r\n"
                + "cdr_call_leg_end_media_info\r\n"
                + "cdr_call_leg_num_date\r\n"
                + "cdr_call_leg_start\r\n"
                + "cdr_call_leg_update\r\n"
                + "cdr_call_num_date\r\n"
                + "cdr_recording\r\n"
                + "cdr_report_result\r\n"
                + "cdr_streaming\r\n"
                + "cdr_task_result\r\n"
                + "gen_table\r\n"
                + "gen_table_column\r\n"
                + "sys_config\r\n"
                + "sys_dept\r\n"
                + "sys_dict_data\r\n"
                + "sys_dict_type\r\n"
                + "sys_logininfor\r\n"
                + "sys_menu\r\n"
                + "sys_notice\r\n"
                + "sys_oper_log\r\n"
                + "sys_post\r\n"
                + "sys_role\r\n"
                + "sys_role_dept\r\n"
                + "sys_role_menu\r\n"
                + "sys_user\r\n"
                + "sys_user_post\r\n"
                + "sys_user_role";
        
        
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("SET FOREIGN_KEY_CHECKS=0;\n");
        IOUtils.readLine(new BufferedReader(new StringReader(tables)), new LineStrProcessor()
        {
            public void process(String lineStr, int lineNumber)
            {
                String table = lineStr.toLowerCase(Locale.ENGLISH);
                if (!table.startsWith("sys_") && !table.startsWith("gen_"))
                {
                    contentBuilder.append("delete from ").append(table).append(";\n");
                }
            }
        });
        contentBuilder.append("SET FOREIGN_KEY_CHECKS=1;");
        IOUtils.copy(contentBuilder.toString(), new FileOutputStream(new File(System.getProperty("user.dir") + "/sql/clean_db.sql")));
    }
}
