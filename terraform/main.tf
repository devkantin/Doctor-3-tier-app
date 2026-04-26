terraform {
  required_version = ">= 1.6"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project
      Environment = var.env
      ManagedBy   = "Terraform"
    }
  }
}

data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_elastic_beanstalk_solution_stack" "tomcat" {
  most_recent = true
  name_regex  = "^64bit Amazon Linux 2023 .* running Tomcat 10\\.1 with Corretto 21$"
}
