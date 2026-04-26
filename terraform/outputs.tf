output "artifact_bucket" {
  description = "S3 bucket for WAR deployment artifacts — set this as S3_ARTIFACT_BUCKET in GitLab CI/CD variables"
  value       = aws_s3_bucket.artifacts.bucket
}

output "app_url" {
  description = "Elastic Beanstalk application URL"
  value       = "http://${aws_elastic_beanstalk_environment.docapp.cname}"
}

output "rds_endpoint" {
  description = "RDS MySQL endpoint (host only)"
  value       = aws_db_instance.mysql.address
}

output "elasticache_endpoint" {
  description = "ElastiCache Memcached cluster address"
  value       = "${aws_elasticache_cluster.memcached.cluster_address}:11211"
}

output "mq_amqps_endpoint" {
  description = "Amazon MQ AMQPS endpoint used by Spring Boot"
  value       = local.mq_endpoint
}

output "mq_console_url" {
  description = "Amazon MQ RabbitMQ management console URL"
  value       = aws_mq_broker.rabbitmq.instances[0].console_url
}

output "beanstalk_env_name" {
  description = "Elastic Beanstalk environment name (use with eb deploy)"
  value       = aws_elastic_beanstalk_environment.docapp.name
}

output "deploy_instructions" {
  description = "How to build and deploy the WAR"
  value       = <<-EOT

    ── Build ────────────────────────────────────────────────────────
    cd ..
    mvn package -DskipTests
    # WAR written to: target/docapp.war

    ── Deploy via EB CLI ────────────────────────────────────────────
    eb init ${var.project} --region ${var.aws_region} --platform "Tomcat"
    eb use ${aws_elastic_beanstalk_environment.docapp.name}
    eb deploy

    ── Or deploy via AWS CLI ────────────────────────────────────────
    S3_BUCKET=$(aws elasticbeanstalk describe-storage-location \
      --query S3Bucket --output text)
    VERSION="v$(date +%Y%m%d%H%M%S)"
    aws s3 cp ../target/docapp.war s3://$S3_BUCKET/docapp/$VERSION.war
    aws elasticbeanstalk create-application-version \
      --application-name ${var.project} \
      --version-label "$VERSION" \
      --source-bundle S3Bucket="$S3_BUCKET",S3Key="docapp/$VERSION.war"
    aws elasticbeanstalk update-environment \
      --environment-name ${aws_elastic_beanstalk_environment.docapp.name} \
      --version-label "$VERSION"

    ── Seed the database ────────────────────────────────────────────
    mysql -h ${aws_db_instance.mysql.address} -u ${var.db_username} -p \
      ${var.db_name} < ../db_backup.sql
  EOT
}
