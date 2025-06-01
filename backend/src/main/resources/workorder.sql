create database wos;
use wos;

drop table if exists work_order;
create table work_order(
    id bigint primary key NOT NULL auto_increment comment 'id',
    code varchar(20) NOT NULL comment '编号',
    type tinyint not null  comment '类型',
    submitter_info varchar(1024) comment '提交信息',
    auditor_info varchar(1024) comment '审批信息',
    distributer_info varchar(1024) comment '派单信息',
    handler_info varchar(1024) comment '处理信息',
    checker_info varchar(1024) comment '确认信息',
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
    order_id bigint NOT NULL comment '订单id',
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


