resource "aws_db_subnet_group" "mysql" {
  name        = "${var.project}-mysql-subnet-group"
  subnet_ids  = aws_subnet.private[*].id
  description = "Subnet group for ${var.project} RDS instance"

  tags = { Name = "${var.project}-mysql-subnet-group" }
}

resource "aws_db_instance" "mysql" {
  identifier            = "${var.project}-mysql"
  engine                = "mysql"
  engine_version        = "8.0"
  instance_class        = var.db_instance_class
  allocated_storage     = 20
  max_allocated_storage = 100
  storage_type          = "gp3"
  storage_encrypted     = true

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.mysql.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  multi_az                = var.db_multi_az
  publicly_accessible     = false
  deletion_protection     = false
  skip_final_snapshot     = true
  backup_retention_period = 7
  backup_window           = "03:00-04:00"
  maintenance_window      = "Mon:04:00-Mon:05:00"

  tags = { Name = "${var.project}-mysql" }
}
