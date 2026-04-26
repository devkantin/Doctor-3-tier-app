resource "aws_mq_broker" "rabbitmq" {
  broker_name         = "${var.project}-rabbitmq"
  engine_type         = "RabbitMQ"
  engine_version      = "3.13"
  host_instance_type  = var.mq_instance_type
  deployment_mode               = "SINGLE_INSTANCE"
  auto_minor_version_upgrade    = true
  publicly_accessible = false

  subnet_ids      = [aws_subnet.private[0].id]
  security_groups = [aws_security_group.mq.id]

  user {
    username = var.mq_username
    password = var.mq_password
  }

  logs {
    general = true
  }

  tags = { Name = "${var.project}-rabbitmq" }
}

locals {
  # endpoints[0] → "amqps://b-xxx.mq.us-east-1.amazonaws.com:5671"
  mq_endpoint = aws_mq_broker.rabbitmq.instances[0].endpoints[0]
  mq_host     = split(":", split("//", local.mq_endpoint)[1])[0]
}
