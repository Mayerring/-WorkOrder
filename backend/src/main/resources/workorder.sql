create database wos;
use wos;
drop table if exists work_order;
create table work_order(
    id bigint primary key NOT NULL auto_increment comment 'id',
    code varchar(64) NOT NULL comment '编号',
    type tinyint not null  comment '类型',
    title varchar(128) comment '工单标题',
    priority_level tinyint comment '优先级',
    status int not null comment '状态',
    create_time bigint not null comment '创建时间',
    update_time bigint comment '更新时间',
    cancel_time  bigint comment '取消时间',
    delete_time  bigint comment '删除时间',
    content text comment '详情',
    accessory_url varchar(128) comment '附件url',
    accessory_name varchar(128) comment '附件名称',
    deleted tinyint not null comment '删除位'
)comment '工单';

drop table if exists handle_log;
create table handle_log(
    id bigint primary key NOT NULL auto_increment comment 'id',
    order_id bigint NOT NULL comment '工单id',
    content text not null comment '内容',
    create_time bigint comment '创建时间'
) comment '操作日志';

drop table if exists message;
create table message(
   id bigint primary key NOT NULL auto_increment comment 'id',
   receiver_id bigint NOT NULL comment '接收人id',
   sender_id bigint NOT NULL comment '发送人id',
   type tinyint NOT NULL comment '类型',
   content text not null comment '内容',
   send_time bigint comment '发送时间'
) comment '消息';

Drop table handle_user_info;
create table handle_user_info(
    id bigint primary key NOT NULL auto_increment comment 'id',
    order_id bigint NOT NULL comment '工单id',
    user_id bigint NOT NULL COMMENT '用户id',
    user_name varchar(64) NOT NULL COMMENT '用户名',
    handle_type tinyint NOT NULL COMMENT '用户类型（1：提交人，2：审核人，3：派单人，4：处理人，5：确认人）',
    company_id bigint NOT NULL COMMENT '公司id',
    company_name varchar(128) NOT NULL COMMENT '公司名',
    department_id bigint NOT NULL COMMENT '部门id',
    department_name varchar(128) NOT NULL COMMENT '部门名',
    handle_time bigint comment '处理时间',
    create_time bigint NOT NULL comment '创建时间',
    update_time bigint comment '修改时间',
    remark varchar(255) comment '备注'
)comment '工单操作信息';



