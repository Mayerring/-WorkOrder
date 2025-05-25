create database wos;
use wos;
create table work_order(
    id bigint primary key NOT NULL auto_increment comment 'id',
    code varchar(20) NOT NULL comment '编号',
    type tinyint comment '类型',
    department_id bigint comment '提出部门id',
    department_name varchar(128) comment '提出部门名称',
    approver varchar(1024) comment '审批人',
    priority_level tinyint comment '优先级',
    handler varchar(1024) comment '处理人' ,
    status tinyint comment '状态',
    create_time bigint comment '创建时间',
    deadline_time bigint comment '截止时间',
    finish_time bigint comment '完成时间',
    accessory_url varchar(128) comment '附件url',
    accessory_name varchar(128) comment '附件名称'
)comment '工单';
alter table work_order modify type tinyint not null comment '类型';
alter table work_order modify status tinyint not null comment '状态';
alter table work_order modify create_time bigint not null comment '创建时间';
alter table work_order add column content text comment '详情';
alter table work_order add column update_time  bigint comment '更新时间' after create_time;
alter table work_order add column cancel_time  bigint comment '取消时间' after update_time;
alter table work_order add column delete_time  bigint comment '删除时间' after cancel_time;
alter table work_order add column deleted tinyint not null comment '删除位';
create table handle_log(
    id bigint primary key NOT NULL auto_increment comment 'id',
    order_id bigint NOT NULL comment '订单id',
    content text not null comment '内容',
    create_time bigint comment '创建时间'
) comment '操作日志';


