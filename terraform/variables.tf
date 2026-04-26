variable "aws_region" {
  description = "AWS region to deploy into"
  type        = string
  default     = "us-east-1"
}

variable "project" {
  description = "Project name — used as a prefix for all resources"
  type        = string
  default     = "docapp"
}

variable "env" {
  description = "Environment label (dev / staging / prod)"
  type        = string
  default     = "prod"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

# ── RDS (MySQL) ────────────────────────────────────────────────────
variable "db_name" {
  description = "MySQL database name"
  type        = string
  default     = "docappdb"
}

variable "db_username" {
  description = "MySQL master username"
  type        = string
  default     = "docapp"
}

variable "db_password" {
  description = "MySQL master password"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "db_multi_az" {
  description = "Enable RDS Multi-AZ for high availability"
  type        = bool
  default     = false
}

# ── ElastiCache (Memcached) ────────────────────────────────────────
variable "cache_node_type" {
  description = "ElastiCache Memcached node type"
  type        = string
  default     = "cache.t3.micro"
}

variable "cache_num_nodes" {
  description = "Number of Memcached cache nodes"
  type        = number
  default     = 1
}

# ── Amazon MQ (RabbitMQ) ───────────────────────────────────────────
variable "mq_username" {
  description = "Amazon MQ admin username"
  type        = string
  default     = "docapp"
}

variable "mq_password" {
  description = "Amazon MQ admin password (minimum 12 characters)"
  type        = string
  sensitive   = true
}

variable "mq_instance_type" {
  description = "Amazon MQ broker instance type"
  type        = string
  default     = "mq.m5.large"
}

# ── Elastic Beanstalk ──────────────────────────────────────────────
variable "eb_solution_stack" {
  description = "Elastic Beanstalk solution stack name"
  type        = string
  default     = "64bit Amazon Linux 2023 v5.13.1 running Tomcat 11 with Corretto 25"
}

variable "ec2_instance_type" {
  description = "EC2 instance type for Beanstalk application servers"
  type        = string
  default     = "t3.small"
}

variable "keypair_name" {
  description = "EC2 key pair name for SSH access (leave blank to disable)"
  type        = string
  default     = ""
}

variable "min_instances" {
  description = "Minimum number of EC2 instances in the autoscaling group"
  type        = number
  default     = 1
}

variable "max_instances" {
  description = "Maximum number of EC2 instances in the autoscaling group"
  type        = number
  default     = 2
}
