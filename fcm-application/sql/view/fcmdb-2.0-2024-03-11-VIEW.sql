-- 预约会议VIEW
create or replace view view_conference_appointment as
select 'fme'     mcu_type, id, create_time, update_time, dept_id, template_id, is_auto_create_template, start_time, end_time, status, extend_minutes, repeat_rate, is_hang_up, repeat_date, password, attendee_limit, is_start, start_failed_reason, type, create_by
from busi_conference_appointment
union all
select 'mcu-zj'  mcu_type, id, create_time, update_time, dept_id, template_id, is_auto_create_template, start_time, end_time, status, extend_minutes, repeat_rate, is_hang_up, repeat_date, password, attendee_limit, is_start, start_failed_reason, type, create_by
from busi_mcu_zj_conference_appointment
union all
select 'mcu-plc' mcu_type, id, create_time, update_time, dept_id, template_id, is_auto_create_template, start_time, end_time, status, extend_minutes, repeat_rate, is_hang_up, repeat_date, password, attendee_limit, is_start, start_failed_reason, type, create_by
from busi_mcu_plc_conference_appointment
union all
select 'mcu-kdc' mcu_type, id, create_time, update_time, dept_id, template_id, is_auto_create_template, start_time, end_time, status, extend_minutes, repeat_rate, is_hang_up, repeat_date, password, attendee_limit, is_start, start_failed_reason, type, create_by
from busi_mcu_kdc_conference_appointment
union all
select 'smc3' mcu_type, id, create_time, update_time, dept_id, template_id, is_auto_create_template, start_time, end_time, status, extend_minutes, repeat_rate, is_hang_up, repeat_date, password, attendee_limit, is_start, start_failed_reason, type, create_by
from busi_mcu_smc3_conference_appointment
union all
select 'smc2' mcu_type, id, create_time, update_time, dept_id, template_id, is_auto_create_template, start_time, end_time, status, extend_minutes, repeat_rate, is_hang_up, repeat_date, password, attendee_limit, is_start, start_failed_reason, type, create_by
from busi_mcu_smc2_conference_appointment
union all
select 'mcu-tencent' mcu_type, id, create_time, update_time, dept_id, template_id, is_auto_create_template, start_time, end_time, status, extend_minutes, repeat_rate, is_hang_up, repeat_date, password, attendee_limit, is_start, start_failed_reason, type, create_by
from busi_mcu_tencent_conference_appointment
union all
select 'mcu-ding' mcu_type, id, create_time, update_time, dept_id, template_id, is_auto_create_template, start_time, end_time, status, extend_minutes, repeat_rate, is_hang_up, repeat_date, password, attendee_limit, is_start, start_failed_reason, type, create_by
from busi_mcu_ding_conference_appointment
union all
select 'mcu-hwcloud' mcu_type, id, create_time, update_time, dept_id, template_id, is_auto_create_template, start_time, end_time, status, extend_minutes, repeat_rate, is_hang_up, repeat_date, password, attendee_limit, is_start, start_failed_reason, type, create_by
from busi_mcu_hwcloud_conference_appointment
;

-- 会议模板VIEW
create or replace view view_template_conference as
select 'fme'     mcu_type, id, create_time, update_time, name, create_user_id, create_user_name, dept_id, call_leg_profile_id, bandwidth, is_auto_call, conference_number, call_profile_id, call_branding_profile_id, type, view_type, is_auto_monitor, stream_url, is_auto_create_conference_number, recording_enabled, create_type, streaming_enabled, master_participant_id, default_view_layout, default_view_is_broadcast, default_view_is_display_self, default_view_is_fill, polling_interval, conference_password, business_field_type, remarks, business_properties, cover, duration_enabled, duration_time, is_auto_create_stream_url, presenter, '' tenant_id, up_cascade_id, up_cascade_mcu_type, up_cascade_type, up_cascade_index
from busi_template_conference
union all
select 'mcu-zj'  mcu_type, id, create_time, update_time, name, create_user_id, create_user_name, dept_id, call_leg_profile_id, bandwidth, is_auto_call, conference_number, call_profile_id, call_branding_profile_id, type, view_type, is_auto_monitor, stream_url, is_auto_create_conference_number, recording_enabled, create_type, streaming_enabled, master_participant_id, default_view_layout, default_view_is_broadcast, default_view_is_display_self, default_view_is_fill, polling_interval, conference_password, business_field_type, remarks, business_properties, cover, duration_enabled, duration_time, is_auto_create_stream_url, presenter, tenant_id, up_cascade_id, up_cascade_mcu_type, up_cascade_type, up_cascade_index
from busi_mcu_zj_template_conference
union all
select 'mcu-plc' mcu_type, id, create_time, update_time, name, create_user_id, create_user_name, dept_id, call_leg_profile_id, bandwidth, is_auto_call, conference_number, call_profile_id, call_branding_profile_id, type, view_type, is_auto_monitor, stream_url, is_auto_create_conference_number, recording_enabled, create_type, streaming_enabled, master_participant_id, default_view_layout, default_view_is_broadcast, default_view_is_display_self, default_view_is_fill, polling_interval, conference_password, business_field_type, remarks, business_properties, cover, duration_enabled, duration_time, is_auto_create_stream_url, presenter, '' tenant_id, up_cascade_id, up_cascade_mcu_type, up_cascade_type, up_cascade_index
from busi_mcu_plc_template_conference
union all
select 'mcu-kdc' mcu_type, id, create_time, update_time, name, create_user_id, create_user_name, dept_id, call_leg_profile_id, bandwidth, is_auto_call, conference_number, call_profile_id, call_branding_profile_id, type, view_type, is_auto_monitor, stream_url, is_auto_create_conference_number, recording_enabled, create_type, streaming_enabled, master_participant_id, default_view_layout, default_view_is_broadcast, default_view_is_display_self, default_view_is_fill, polling_interval, conference_password, business_field_type, remarks, business_properties, cover, duration_enabled, duration_time, is_auto_create_stream_url, presenter, '' tenant_id, up_cascade_id, up_cascade_mcu_type, up_cascade_type, up_cascade_index
from busi_mcu_kdc_template_conference
union all
select 'smc3' mcu_type, id, create_time, update_time, name, create_user_id, create_user_name, dept_id, call_leg_profile_id, bandwidth, is_auto_call, conference_number, call_profile_id, call_branding_profile_id, type, view_type, is_auto_monitor, stream_url, is_auto_create_conference_number, recording_enabled, create_type, streaming_enabled, master_participant_id, default_view_layout, default_view_is_broadcast, default_view_is_display_self, default_view_is_fill, polling_interval, conference_password, business_field_type, remarks, business_properties, cover, duration_enabled, duration_time, is_auto_create_stream_url, presenter, '' tenant_id, up_cascade_id, up_cascade_mcu_type, up_cascade_type, up_cascade_index
from busi_mcu_smc3_template_conference
union all
select 'smc2' mcu_type, id, create_time, update_time, name, create_user_id, create_user_name, dept_id, call_leg_profile_id, bandwidth, is_auto_call, conference_number, call_profile_id, call_branding_profile_id, type, view_type, is_auto_monitor, stream_url, is_auto_create_conference_number, recording_enabled, create_type, streaming_enabled, master_participant_id, default_view_layout, default_view_is_broadcast, default_view_is_display_self, default_view_is_fill, polling_interval, conference_password, business_field_type, remarks, business_properties, cover, duration_enabled, duration_time, is_auto_create_stream_url, presenter, '' tenant_id, up_cascade_id, up_cascade_mcu_type, up_cascade_type, up_cascade_index
from busi_mcu_smc2_template_conference
union all
select 'mcu-tencent' mcu_type, id, create_time, update_time, name, create_user_id, create_user_name, dept_id, call_leg_profile_id, bandwidth, is_auto_call, conference_number, call_profile_id, call_branding_profile_id, type, view_type, is_auto_monitor, stream_url, is_auto_create_conference_number, recording_enabled, create_type, streaming_enabled, master_participant_id, default_view_layout, default_view_is_broadcast, default_view_is_display_self, default_view_is_fill, polling_interval, conference_password, business_field_type, remarks, business_properties, cover, duration_enabled, duration_time, is_auto_create_stream_url, presenter, '' tenant_id, up_cascade_id, up_cascade_mcu_type, up_cascade_type, up_cascade_index
from busi_mcu_tencent_template_conference
union all
select 'mcu-ding' mcu_type, id, create_time, update_time, name, create_user_id, create_user_name, dept_id, call_leg_profile_id, bandwidth, is_auto_call, conference_number, call_profile_id, call_branding_profile_id, type, view_type, is_auto_monitor, stream_url, is_auto_create_conference_number, recording_enabled, create_type, streaming_enabled, master_participant_id, default_view_layout, default_view_is_broadcast, default_view_is_display_self, default_view_is_fill, polling_interval, conference_password, business_field_type, remarks, business_properties, cover, duration_enabled, duration_time, is_auto_create_stream_url, presenter, '' tenant_id, up_cascade_id, up_cascade_mcu_type, up_cascade_type, up_cascade_index
from busi_mcu_ding_template_conference
union all
select 'mcu-hwcloud' mcu_type, id, create_time, update_time, name, create_user_id, create_user_name, dept_id, call_leg_profile_id, bandwidth, is_auto_call, conference_number, call_profile_id, call_branding_profile_id, type, view_type, is_auto_monitor, stream_url, is_auto_create_conference_number, recording_enabled, create_type, streaming_enabled, master_participant_id, default_view_layout, default_view_is_broadcast, default_view_is_display_self, default_view_is_fill, polling_interval, conference_password, business_field_type, remarks, business_properties, cover, duration_enabled, duration_time, is_auto_create_stream_url, presenter, '' tenant_id, up_cascade_id, up_cascade_mcu_type, up_cascade_type, up_cascade_index
from busi_mcu_hwcloud_template_conference
;

-- 会议模板与会者
create or replace view view_template_participant as
select 'fme'     mcu_type, id, create_time, update_time, uuid, attend_type, template_conference_id, terminal_id, weight, business_properties
from busi_template_participant
union all
select 'mcu-zj'  mcu_type, id, create_time, update_time, uuid, attend_type, template_conference_id, terminal_id, weight, business_properties
from busi_mcu_zj_template_participant
union all
select 'mcu-plc' mcu_type, id, create_time, update_time, uuid, attend_type, template_conference_id, terminal_id, weight, business_properties
from busi_mcu_plc_template_participant
union all
select 'mcu-kdc' mcu_type, id, create_time, update_time, uuid, attend_type, template_conference_id, terminal_id, weight, business_properties
from busi_mcu_kdc_template_participant
union all
select 'smc3'  mcu_type, id, create_time, update_time, uuid, attend_type, template_conference_id, terminal_id, weight, business_properties
from busi_mcu_smc3_template_participant
union all
select 'smc2' mcu_type, id, create_time, update_time, uuid, attend_type, template_conference_id, terminal_id, weight, business_properties
from busi_mcu_smc2_template_participant
union all
select 'mcu-tencent' mcu_type, id, create_time, update_time, uuid, attend_type, template_conference_id, terminal_id, weight, business_properties
from busi_mcu_tencent_template_participant
union all
select 'mcu-ding' mcu_type, id, create_time, update_time, uuid, attend_type, template_conference_id, terminal_id, weight, business_properties
from busi_mcu_ding_template_participant
union all
select 'mcu-hwcloud' mcu_type, id, create_time, update_time, uuid, attend_type, template_conference_id, terminal_id, weight, business_properties
from busi_mcu_hwcloud_template_participant
;

