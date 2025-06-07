-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for looksy_ytta
CREATE DATABASE IF NOT EXISTS `looksy_ytta` /*!40100 DEFAULT CHARACTER SET armscii8 COLLATE armscii8_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `looksy_ytta`;

-- Dumping structure for table looksy_ytta.cart_items
CREATE TABLE IF NOT EXISTS `cart_items` (
  `quantity` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1vhvont0fdtramle6nmghntj7` (`user_id`,`product_id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK709eickf3kc0dujx3ub9i7btf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=armscii8 COLLATE=armscii8_bin;

-- Dumping data for table looksy_ytta.cart_items: ~1 rows (approximately)
REPLACE INTO `cart_items` (`quantity`, `id`, `product_id`, `user_id`) VALUES
	(1, 1, 1, 2);

-- Dumping structure for table looksy_ytta.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `total_amount` decimal(10,2) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_date` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  `status` enum('CANCELLED','COMPLETED','PENDING','PROCESSING') COLLATE armscii8_bin NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=armscii8 COLLATE=armscii8_bin;

-- Dumping data for table looksy_ytta.orders: ~0 rows (approximately)

-- Dumping structure for table looksy_ytta.order_items
CREATE TABLE IF NOT EXISTS `order_items` (
  `price_at_purchase` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=armscii8 COLLATE=armscii8_bin;

-- Dumping data for table looksy_ytta.order_items: ~0 rows (approximately)

-- Dumping structure for table looksy_ytta.products
CREATE TABLE IF NOT EXISTS `products` (
  `price` decimal(10,2) NOT NULL,
  `stock` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` text COLLATE armscii8_bin,
  `image_url` varchar(255) COLLATE armscii8_bin DEFAULT NULL,
  `name` varchar(255) COLLATE armscii8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=armscii8 COLLATE=armscii8_bin;

-- Dumping data for table looksy_ytta.products: ~3 rows (approximately)
REPLACE INTO `products` (`price`, `stock`, `id`, `description`, `image_url`, `name`) VALUES
	(90000.00, 900, 1, 'baju', '', 'kyrsu'),
	(800.00, 90, 2, 'celana', NULL, 'celana '),
	(9000.00, 90, 3, 'kerudung', '', 'kerudung');

-- Dumping structure for table looksy_ytta.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(100) COLLATE armscii8_bin NOT NULL,
  `username` varchar(100) COLLATE armscii8_bin NOT NULL,
  `password` varchar(255) COLLATE armscii8_bin NOT NULL,
  `role` enum('ADMIN','USER') COLLATE armscii8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=armscii8 COLLATE=armscii8_bin;

-- Dumping data for table looksy_ytta.users: ~2 rows (approximately)
REPLACE INTO `users` (`id`, `email`, `username`, `password`, `role`) VALUES
	(1, 'admin123@gmail.com', 'admin', '$2a$10$oDOK4Rei4ufFk/0kisUcZuxYnkJ4U1ChIyZDCjAOzFZRKAVOl.ymq', 'ADMIN'),
	(2, 'zaynanindira@gamil.com', 'xxyeblee', '$2a$10$WAkWH0bEw1QlmrNITfJwEO/pChEAbKrfm1CPkslp4Tf2gbk1UG73.', 'USER');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
