resource "aws_elastic_beanstalk_application" "docapp" {
  name        = var.project
  description = "Doctor Appointment App — Spring Boot WAR on Tomcat"
}

resource "aws_elastic_beanstalk_environment" "docapp" {
  name                = "${var.project}-${var.env}"
  application         = aws_elastic_beanstalk_application.docapp.name
  solution_stack_name = var.eb_solution_stack
  tier                = "WebServer"

  # ── VPC placement ─────────────────────────────────────────────
  setting {
    namespace = "aws:ec2:vpc"
    name      = "VPCId"
    value     = aws_vpc.main.id
  }
  setting {
    namespace = "aws:ec2:vpc"
    name      = "Subnets"
    value     = join(",", aws_subnet.private[*].id)
  }
  setting {
    namespace = "aws:ec2:vpc"
    name      = "ELBSubnets"
    value     = join(",", aws_subnet.public[*].id)
  }
  setting {
    namespace = "aws:ec2:vpc"
    name      = "ELBScheme"
    value     = "public"
  }

  # ── Instance configuration ─────────────────────────────────────
  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = var.ec2_instance_type
  }
  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "IamInstanceProfile"
    value     = aws_iam_instance_profile.beanstalk_ec2.name
  }
  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "SecurityGroups"
    value     = aws_security_group.beanstalk.id
  }
  dynamic "setting" {
    for_each = var.keypair_name != "" ? [var.keypair_name] : []
    content {
      namespace = "aws:autoscaling:launchconfiguration"
      name      = "EC2KeyName"
      value     = setting.value
    }
  }

  # ── Autoscaling ────────────────────────────────────────────────
  setting {
    namespace = "aws:autoscaling:asg"
    name      = "MinSize"
    value     = tostring(var.min_instances)
  }
  setting {
    namespace = "aws:autoscaling:asg"
    name      = "MaxSize"
    value     = tostring(var.max_instances)
  }

  # ── Load balancer ──────────────────────────────────────────────
  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "EnvironmentType"
    value     = "LoadBalanced"
  }
  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "LoadBalancerType"
    value     = "application"
  }
  setting {
    namespace = "aws:elbv2:loadbalancer"
    name      = "SecurityGroups"
    value     = aws_security_group.alb.id
  }

  # ── Health check ───────────────────────────────────────────────
  setting {
    namespace = "aws:elasticbeanstalk:application"
    name      = "Application Healthcheck URL"
    value     = "/login"
  }
  setting {
    namespace = "aws:elasticbeanstalk:healthreporting:system"
    name      = "SystemType"
    value     = "enhanced"
  }

  # ── Rolling deployments ────────────────────────────────────────
  setting {
    namespace = "aws:elasticbeanstalk:command"
    name      = "DeploymentPolicy"
    value     = "Rolling"
  }
  setting {
    namespace = "aws:elasticbeanstalk:command"
    name      = "BatchSizeType"
    value     = "Percentage"
  }
  setting {
    namespace = "aws:elasticbeanstalk:command"
    name      = "BatchSize"
    value     = "50"
  }

  # ── Application environment variables ─────────────────────────
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "DB_HOST"
    value     = aws_db_instance.mysql.address
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "DB_NAME"
    value     = var.db_name
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "DB_USER"
    value     = var.db_username
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "DB_PASS"
    value     = var.db_password
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "RABBITMQ_HOST"
    value     = local.mq_host
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "RABBITMQ_PORT"
    value     = "5671"
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "RABBITMQ_USER"
    value     = var.mq_username
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "RABBITMQ_PASS"
    value     = var.mq_password
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "RABBITMQ_SSL"
    value     = "true"
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "MEMCACHED_HOST"
    value     = aws_elasticache_cluster.memcached.cluster_address
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "MEMCACHED_PORT"
    value     = "11211"
  }

  depends_on = [
    aws_db_instance.mysql,
    aws_elasticache_cluster.memcached,
    aws_mq_broker.rabbitmq,
  ]

  tags = { Name = "${var.project}-${var.env}" }
}
