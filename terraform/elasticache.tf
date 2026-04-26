resource "aws_elasticache_subnet_group" "memcached" {
  name        = "${var.project}-memcached-subnet-group"
  subnet_ids  = aws_subnet.private[*].id
  description = "Subnet group for ${var.project} Memcached cluster"

  tags = { Name = "${var.project}-memcached-subnet-group" }
}

resource "aws_elasticache_cluster" "memcached" {
  cluster_id           = "${var.project}-memcached"
  engine               = "memcached"
  node_type            = var.cache_node_type
  num_cache_nodes      = var.cache_num_nodes
  parameter_group_name = "default.memcached1.6"
  port                 = 11211

  subnet_group_name  = aws_elasticache_subnet_group.memcached.name
  security_group_ids = [aws_security_group.elasticache.id]

  tags = { Name = "${var.project}-memcached" }
}
