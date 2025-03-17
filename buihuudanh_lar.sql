-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 07, 2024 at 02:41 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `buihuudanh_lar`
--

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_banner`
--

CREATE TABLE `cdtt_banner` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `link` varchar(1000) NOT NULL,
  `position` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `created_by` int(10) UNSIGNED DEFAULT NULL,
  `updated_by` int(10) UNSIGNED DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_banner`
--

INSERT INTO `cdtt_banner` (`id`, `name`, `image`, `link`, `position`, `description`, `created_by`, `updated_by`, `created_at`, `updated_at`, `status`) VALUES
(15, 'quảng cáo', '20240605102636.jpg', 'asd adsadsa a', 'slideshow', NULL, 1, 1, '2024-06-05 03:26:36', '2024-06-16 04:25:52', 1),
(16, 'áđasda', '20240605102700.jpg', 'ád', 'slideshow', NULL, 1, 1, '2024-06-05 03:27:00', '2024-06-16 04:25:50', 1),
(17, 'quảng cáo', '20241015040211.png', 'quang cao 1', 'slideshow', 'day la quang cao 1', 1, 1, '2024-09-25 18:34:14', '2024-10-14 21:02:11', 1);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_brand`
--

CREATE TABLE `cdtt_brand` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(1000) NOT NULL,
  `slug` varchar(1000) NOT NULL,
  `image` varchar(1000) DEFAULT NULL,
  `sort_order` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `description` varchar(1000) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int(10) UNSIGNED NOT NULL,
  `updated_by` int(10) UNSIGNED DEFAULT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_brand`
--

INSERT INTO `cdtt_brand` (`id`, `name`, `slug`, `image`, `sort_order`, `description`, `created_at`, `updated_at`, `created_by`, `updated_by`, `status`) VALUES
(1, 'ASUS', 'asus', '20240617133726.jpg', 0, 'Mô tả SEO', '2020-07-03 02:06:19', '2024-09-22 19:48:21', 1, 1, 1),
(2, 'Playstation', 'playstation', '20240923034516.jpg', 0, 'Mô tả Hàn Quốc', '2020-07-03 02:06:19', '2024-10-27 18:07:48', 1, 1, 2),
(3, 'Thái Lan', 'thai-lan', '', 0, 'Mô tả SEO', '2020-07-03 02:06:19', '2024-06-13 10:04:00', 1, 1, 0),
(4, 'Nhật Bản', 'nhat-ban', '', 0, 'Mô tả SEO', '2020-07-03 02:06:19', '2024-06-13 10:04:53', 1, 1, 0),
(17, 'SAMSUNG', 'samsung', '20240617133929.jpg', 0, 'sd sa', '2024-06-04 20:45:34', '2024-09-22 19:48:56', 1, 1, 1),
(18, 'APPLE', 'apple', '20240617133946.jpg', 0, 'dfij lkjsfo', '2024-06-04 20:46:59', '2024-06-17 06:39:46', 1, 1, 1),
(19, 'OPPO', 'oppo', '20240617134002.jpg', 0, NULL, '2024-06-04 20:48:09', '2024-06-17 06:40:02', 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_category`
--

CREATE TABLE `cdtt_category` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(1000) NOT NULL,
  `slug` varchar(1000) NOT NULL,
  `image` varchar(1000) DEFAULT NULL,
  `parent_id` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `sort_order` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `description` varchar(1000) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int(10) UNSIGNED NOT NULL,
  `updated_by` int(10) UNSIGNED DEFAULT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_category`
--

INSERT INTO `cdtt_category` (`id`, `name`, `slug`, `image`, `parent_id`, `sort_order`, `description`, `created_at`, `updated_at`, `created_by`, `updated_by`, `status`) VALUES
(56, 'Laptop', 'laptop', '20240611032328.png', 0, 0, 'Những sản phẩm là laptop.', '2024-06-10 20:23:28', '2024-06-15 08:16:55', 1, 1, 1),
(58, 'Tivi', 'tivi', '20240611032426.png', 0, 0, 'Những sản phẩm là tivi.', '2024-06-10 20:24:26', '2024-10-27 18:19:52', 1, 1, 2),
(62, 'Tai nghe', 'tai-nghe', '20240617132946.png', 0, 0, NULL, '2024-06-17 06:29:46', '2024-10-27 18:19:48', 1, 1, 2),
(63, 'Phụ kiện', 'phu-kien', '20240617133005.png', 0, 0, NULL, '2024-06-17 06:30:05', '2024-10-27 18:19:50', 1, 1, 2),
(64, 'Smartphone', 'smartphone', '20241029022653.png', 0, 0, NULL, '2024-06-17 06:30:31', '2024-10-28 19:26:53', 1, 1, 1),
(65, 'Watch', 'watch', '20241029022634.png', 0, 0, NULL, '2024-06-17 06:31:03', '2024-10-28 19:26:34', 1, 1, 1),
(66, 'Tablet', 'tablet', '20240617133146.png', 0, 0, NULL, '2024-06-17 06:31:46', '2024-06-17 06:31:46', 1, NULL, 1),
(67, 'Máy in', 'may-in', '20240617133203.png', 0, 0, NULL, '2024-06-17 06:32:03', '2024-09-22 18:37:24', 1, 1, 2),
(68, 'Máy Tính', 'may-tinh', '20240923022230.png', 0, 0, NULL, '2024-06-17 06:34:03', '2024-09-22 19:54:04', 1, 1, 2);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_contact`
--

CREATE TABLE `cdtt_contact` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` mediumtext NOT NULL,
  `replay_id` int(10) UNSIGNED DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL,
  `updated_by` int(10) UNSIGNED DEFAULT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_contact`
--

INSERT INTO `cdtt_contact` (`id`, `user_id`, `name`, `email`, `phone`, `title`, `content`, `replay_id`, `created_at`, `updated_at`, `updated_by`, `status`) VALUES
(9, NULL, 'Hồ DIên Lợi', 'hodienloi@gmail.com', '0987654321', 'Ho hoi', 'Tào lao', 1, '2024-07-01 02:56:47', '2024-06-30 12:56:47', 1, 0),
(10, 1, 'Sản Phẩm', 'tavanchien360@gmail.com', '0373974675', 'dssfsaw da ad  aadada', 'svdgglkj kfja lffj alfa', 0, '2024-09-30 10:20:26', '2024-09-29 20:20:26', 1, 1),
(19, NULL, 'Bùi Hữu Danh', 'huudanh@gmail.com', '0755425639', 'Yêu cầu hoàn tiền', 'Tôi viết liên hệ yêu cầu hoàn tiền', 0, '2024-10-31 16:33:19', '2024-10-31 09:33:19', NULL, 1),
(21, NULL, 'Huu Danh', 'dan@gmail.com', '0123456789', 'wiejfiqoejf', 'welf,qwpef', 0, '2024-11-05 14:33:42', '2024-11-05 07:33:42', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_menu`
--

CREATE TABLE `cdtt_menu` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(1000) NOT NULL,
  `link` varchar(1000) NOT NULL,
  `sort_order` int(10) UNSIGNED DEFAULT 0,
  `parent_id` int(10) UNSIGNED DEFAULT 0,
  `type` varchar(100) NOT NULL,
  `position` varchar(255) NOT NULL,
  `table_id` int(10) UNSIGNED DEFAULT 0,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int(10) UNSIGNED NOT NULL,
  `updated_by` int(10) UNSIGNED DEFAULT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_menu`
--

INSERT INTO `cdtt_menu` (`id`, `name`, `link`, `sort_order`, `parent_id`, `type`, `position`, `table_id`, `created_at`, `updated_at`, `created_by`, `updated_by`, `status`) VALUES
(83, 'Trang chủ', 'trang-chu', 0, 0, 'custom', 'mainmenu', 0, '2024-06-20 09:24:46', '2024-06-20 09:24:47', 1, 1, 1),
(85, 'Danh mục', '', 0, 0, 'custom', 'mainmenu', 0, '2024-06-21 03:20:12', '2024-06-21 03:20:22', 1, 1, 1),
(90, 'Máy Tính', 'danh-muc/may-tinh', 0, 85, 'category', 'mainmenu', 68, '2024-06-22 11:04:49', '2024-10-27 18:19:06', 1, 1, 2),
(91, 'Máy in', 'danh-muc/may-in', 0, 85, 'category', 'mainmenu', 67, '2024-06-22 11:04:49', '2024-10-27 18:19:09', 1, 1, 2),
(92, 'Tablet', 'danh-muc/tablet', 0, 85, 'category', 'mainmenu', 66, '2024-06-22 11:04:49', '2024-06-22 11:04:57', 1, 1, 1),
(93, 'Watch', 'danh-muc/watch', 0, 85, 'category', 'mainmenu', 65, '2024-06-22 11:04:49', '2024-06-22 11:04:59', 1, 1, 1),
(94, 'Smartphone', 'danh-muc/smartphone', 0, 85, 'category', 'mainmenu', 64, '2024-06-22 11:04:49', '2024-06-22 11:05:04', 1, 1, 1),
(95, 'Phụ kiện', 'danh-muc/phu-kien', 0, 85, 'category', 'mainmenu', 63, '2024-06-22 11:04:49', '2024-10-27 18:20:08', 1, 1, 2),
(96, 'Tai nghe', 'danh-muc/tai-nghe', 0, 85, 'category', 'mainmenu', 62, '2024-06-22 11:04:49', '2024-10-27 18:20:09', 1, 1, 2),
(97, 'Tivi', 'danh-muc/tivi', 0, 85, 'category', 'mainmenu', 58, '2024-06-22 11:04:49', '2024-10-27 18:20:11', 1, 1, 2),
(98, 'Laptop', 'danh-muc/laptop', 0, 85, 'category', 'mainmenu', 56, '2024-06-22 11:04:49', '2024-06-22 11:05:02', 1, 1, 1),
(100, 'Thương hiệu', 'thuong-hieu', 0, 0, 'custom', 'mainmenu', 0, '2024-06-23 23:36:10', '2024-06-23 23:36:18', 1, 1, 1),
(101, 'Sản Phẩm', 'san-pham', 0, 0, 'custom', 'mainmenu', 0, '2024-06-23 23:37:52', '2024-06-23 23:37:54', 1, 1, 1),
(102, 'Liên hệ', 'lien-he', 0, 0, 'custom', 'mainmenu', 0, '2024-06-23 23:38:19', '2024-06-23 23:38:19', 1, NULL, 1),
(103, 'OPPO', 'thuong-hieu/oppo', 0, 100, 'brand', 'mainmenu', 19, '2024-06-23 23:39:01', '2024-06-23 23:41:44', 1, 1, 1),
(104, 'APPLE', 'thuong-hieu/apple', 0, 100, 'brand', 'mainmenu', 18, '2024-06-23 23:39:01', '2024-06-23 23:41:45', 1, 1, 1),
(105, 'SAMSUNG', 'thuong-hieu/samsung', 0, 100, 'brand', 'mainmenu', 17, '2024-06-23 23:39:01', '2024-10-27 18:19:02', 1, 1, 1),
(106, 'ASUS', 'thuong-hieu/asus', 0, 100, 'brand', 'mainmenu', 1, '2024-06-23 23:39:01', '2024-06-23 23:41:47', 1, 1, 1),
(109, 'Chủ đề', 'chu-de', 0, 0, 'custom', 'mainmenu', 0, '2024-06-25 20:15:11', '2024-06-25 20:15:11', 1, NULL, 1),
(110, 'công nghệ', 'chu-de/cong-nghe', 0, 109, 'topic', 'mainmenu', 8, '2024-06-25 20:20:47', '2024-06-25 20:44:14', 1, 1, 1),
(111, 'blog', 'chu-de/blog', 0, 109, 'topic', 'mainmenu', 9, '2024-06-25 20:20:47', '2024-06-25 20:44:16', 1, 1, 1),
(112, 'Dịch vụ', 'chu-de/dich-vu', 0, 109, 'topic', 'mainmenu', 4, '2024-06-25 20:20:47', '2024-06-25 20:44:16', 1, 1, 1),
(113, 'Tin tức', 'chu-de/tin-tuc', 0, 109, 'topic', 'mainmenu', 3, '2024-06-25 20:20:47', '2024-06-25 20:44:17', 1, 1, 1),
(114, 'Bài viết', 'bai-viet', 0, 0, 'custom', 'mainmenu', 0, '2024-06-25 20:50:59', '2024-06-25 20:50:59', 1, NULL, 1),
(116, 'Giới thiệu', 'trang-don/gioi-thieu', 0, 0, 'page', 'mainmenu', 23, '2024-06-29 10:54:09', '2024-09-25 20:08:37', 1, 1, 1),
(129, 'Playstation', 'thuong-hieu/playstation', 0, 100, 'brand', 'mainmenu', 2, '2024-10-27 17:56:00', '2024-10-27 18:08:13', 1, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_order`
--

CREATE TABLE `cdtt_order` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `name` varchar(1000) DEFAULT NULL,
  `phone` varchar(13) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` varchar(1000) DEFAULT NULL,
  `note` tinytext DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` int(11) DEFAULT NULL,
  `status` tinyint(3) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `cdtt_order`
--

INSERT INTO `cdtt_order` (`id`, `user_id`, `name`, `phone`, `email`, `address`, `note`, `created_at`, `updated_at`, `updated_by`, `status`) VALUES
(12, 10, 'Bui Huu Danh', '0755425639', 'huudanh@gmail.com', 'Thu Duc', 'Your order note', '2024-11-05 16:54:17', '2024-11-05 16:54:17', NULL, 1),
(13, 10, 'Bui Huu Danh', '0755425639', 'huudanh@gmail.com', 'Thu Duc', 'Your order note', '2024-11-05 16:55:21', '2024-11-05 16:55:21', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_orderdetail`
--

CREATE TABLE `cdtt_orderdetail` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `order_id` int(10) UNSIGNED NOT NULL,
  `product_id` int(10) UNSIGNED NOT NULL,
  `price` double NOT NULL,
  `qty` int(10) UNSIGNED NOT NULL,
  `discount` double NOT NULL,
  `amount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_orderdetail`
--

INSERT INTO `cdtt_orderdetail` (`id`, `order_id`, `product_id`, `price`, `qty`, `discount`, `amount`) VALUES
(72, 12, 1028, 4200000, 1, 0, 4200000),
(73, 13, 1043, 3920000, 2, 0, 7840000);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_post`
--

CREATE TABLE `cdtt_post` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `topic_id` int(10) UNSIGNED DEFAULT NULL,
  `title` varchar(1000) NOT NULL,
  `slug` varchar(1000) NOT NULL,
  `detail` text NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `type` enum('post','page') NOT NULL DEFAULT 'post',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int(10) UNSIGNED NOT NULL,
  `updated_by` int(10) UNSIGNED DEFAULT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_post`
--

INSERT INTO `cdtt_post` (`id`, `topic_id`, `title`, `slug`, `detail`, `description`, `image`, `type`, `created_at`, `updated_at`, `created_by`, `updated_by`, `status`) VALUES
(18, 8, 'Điện thoại thông minh', 'dien-thoai-thong-minh', 'Điện thoại thông minh sẽ hỗ trợ bạn cải thiện trí nhớ, lưu giữ những thông tin quan trọng. Những lúc bận rộn với công việc hay học tập khiến bạn không nhớ hết tất cả thì chỉ cần mở điện thoại và ghi chép vào là bạn có thể ghi nhớ hết mọi việc cần thiết.\r\nThiết bị còn là công cụ giúp bạn đánh dấu các lịch trình làm việc hàng ngày. Có thể nói, smartphone có khả năng thay thế một cuốn sổ ghi chép giúp bạn kiểm soát tốt công việc của mình.\r\nChức năng chính của một chiếc điện thoại là nghe gọi, liên lạc với mọi người. Bạn có thể gọi điện thoại hỏi thăm, tâm sự với bạn bè, người thân hay thầy cô. Điện thoại cho đối tác, đồng nghiệp bàn công việc.\r\nKhông những thế, những lúc xảy ra sự cố bất ngờ, khẩn cấp thì chiếc điện thoại thông minh sẽ giúp liên lạc nhanh chóng. Đồng thời, người dùng có thể sử dụng điện thoại thông minh để trả lời tin nhắn hoặc trả lời mail công việc một cách nhanh chóng và liên lợi.', 'Những lợi ích của điện thoại thông minh trong cuộc sống bạn không nên bỏ qua', '20240625160840.jpg', 'post', '2024-06-25 20:02:32', '2024-06-25 20:02:49', 1, 1, 1),
(19, 3, 'Đây là những iPhone, iPad và máy Mac tương thích với Apple Intelligence', 'day-la-nhung-iphone-ipad-va-may-mac-tuong-thich-voi-apple-intelligence', 'Một trong những điểm nhấn chính của WWDC năm nay là thông báo về \"Apple Intelligence\" - một công cụ AI mới của Apple dành cho iPhone, iPad và máy Mac. Tuy nhiên, mặc dù nhiều thiết bị có thể cập nhật lên iOS 18, iPadOS 18 và macOS 15, nhưng chỉ một số ít trong số những thiết bị trên có thể sử dụng tính năng Apple Intelligence mới.\r\nApple Intelligence là gì?\r\nApple Intelligence là tên mà Apple gọi các tính năng mới dựa trên AI của hãng. Ví dụ, người dùng giờ đây có thể yêu cầu hệ thống diễn đạt lại văn bản, tóm tắt tin nhắn hoặc email, tạo hình ảnh và thậm chí cả biểu tượng cảm xúc. Siri cũng đã được cập nhật với AI, giờ đây Siri có thể hiểu ngữ cảnh của những gì hiển thị trên màn hình và cho phép người dùng kiểm soát nhiều khía cạnh hơn của thiết bị.\r\niPhone với chip A17 Pro trở lên hiện bao gồm:\r\n\r\niPhone 15 Pro.\r\niPhone 15 Pro Max.\r\niPad với chip M1 trở lên hiện bao gồm:\r\n\r\niPad Pro M1.\r\niPad Pro M2.\r\niPad Pro M4.\r\niPad Air M1.\r\niPad Air M2.', 'Danh sách các iPhone, iPad và máy Mac tương thích với Apple Intelligence', '20240625171124.webp', 'post', '2024-06-25 20:02:14', '2024-06-25 20:02:51', 1, 1, 1),
(20, 8, 'Hướng dẫn cách cập nhật iOS 18 Beta với nhiều tính năng mới siêu hay và bảo mật hơn', 'huong-dan-cach-cap-nhat-ios-18-beta-voi-nhieu-tinh-nang-moi-sieu-hay-va-bao-mat-hon', 'iOS 18 chính thức ra mắt trong sự kiện WWDC24 vừa qua mang đến một loạt tính năng mới và cải tiến hấp dẫn cho người dùng điện thoại iPhone. Tuy nhiên hiện tại người dùng chỉ có thể tải về bản iOS 18 Beta để trải nghiệm. Nếu bạn đang háo hức muốn trải nghiệm những thay đổi này, hãy cùng mình tìm hiểu cách cập nhật iOS 18 Beta đơn giản và nhanh chóng. Hãy cùng cập nhật lên iOS 18 để tận hưởng các tính năng mới ngay thôi nào!\r\nChỉ với 3 bước đơn giản, bạn đã có thể cài đặt iOS 18 Beta về iPhone của mình rồi, hãy trải nghiệm ngay các tính năng mới của phiên bản iOS 18 nhé. Đây chỉ là bản Beta trải nghiệm, vậy iOS 18 chính thức khi nào ra mắt? Bạn có thắc mắc không? Kéo ngay xuống dưới để tìm hiểu nhé.', 'Cách cập nhật iOS 18 Beta', '20240625171401.webp', 'post', '2024-06-25 20:02:23', '2024-06-25 20:02:50', 1, 1, 1),
(22, 3, 'Tất tần tật về chương trình ưu đãi app Quà Tặng Vip, link tải và cách sử dụng', 'tat-tan-tat-ve-chuong-trinh-uu-dai-app-qua-tang-vip-link-tai-va-cach-su-dung', '<h2><strong>Giới thiệu về app Qu&agrave; Tặng Vip</strong></h2>\r\n\r\n<p>&nbsp;</p>\r\n\r\n<p>Ứng dụng Qu&agrave; Tặng Vip cung cấp nhiều dịch vụ cho kh&aacute;ch h&agrave;ng chỉ với v&agrave;i thao t&aacute;c đơn giản th&ocirc;ng qua app, kh&aacute;ch h&agrave;ng c&oacute; thể t&iacute;ch điểm mỗi lần mua h&agrave;ng v&agrave; sử dụng điểm thưởng để quy đổi th&agrave;nh ưu đ&atilde;i khi mua h&agrave;ng ở lần kế tiếp.</p>\r\n\r\n<p>Ngo&agrave;i ra, app Qu&agrave; Tặng Vip c&oacute; nhiều t&iacute;nh năng hay kh&aacute;c như: T&igrave;m kiếm cửa h&agrave;ng gần nhất, theo d&otilde;i đơn h&agrave;ng, đặt lịch sửa chữa,... Từ ng&agrave;y 1/11/2022, app Qu&agrave; Tặng Vip c&oacute; c&aacute;c chương tr&igrave;nh ưu đ&atilde;i như sau:</p>\r\n\r\n<p><strong>Thời gian diễn ra chương tr&igrave;nh:</strong>&nbsp;Từ ng&agrave;y 1/11/2022</p>\r\n\r\n<p><strong>Địa điểm &aacute;p dụng chương tr&igrave;nh:</strong></p>\r\n\r\n<ul>\r\n	<li>H&igrave;nh thức mua trực tiếp: &Aacute;p dụng chương tr&igrave;nh t&iacute;ch điểm v&agrave; sử dụng điểm tại c&aacute;c cửa h&agrave;ng Điện m&aacute;y XANH tr&ecirc;n toàn qu&ocirc;́c.</li>\r\n	<li>H&igrave;nh thức mua online: &Aacute;p dụng chương tr&igrave;nh t&iacute;ch điểm v&agrave; sử dụng điểm tại trang website của Điện m&aacute;y XANH&nbsp;<a href=\"https://www.dienmayxanh.com/\" target=\"_blank\">www.dienmayxanh.com</a></li>\r\n</ul>\r\n\r\n<p><strong>Đối tượng tham gia:</strong></p>\r\n\r\n<p>Kh&aacute;ch h&agrave;ng c&oacute; t&agrave;i khoản tr&ecirc;n ứng dụng &quot;Qu&agrave; Tặng VIP&quot; v&agrave; c&oacute; giao dịch mua h&agrave;ng th&agrave;nh c&ocirc;ng tại c&aacute;c đối t&aacute;c được c&ocirc;ng bố tại ứng dụng Qu&agrave; Tặng VIP.</p>\r\n\r\n<p><strong>Nội dung chương tr&igrave;nh:</strong></p>\r\n\r\n<p>Khi kh&aacute;ch h&agrave;ng mua 1 sản phẩm bất k&igrave; nằm trong danh s&aacute;ch c&aacute;c ng&agrave;nh h&agrave;ng được &aacute;p dụng cho chương tr&igrave;nh. Điểm được t&iacute;ch sẽ t&iacute;nh theo tỉ lệ phần trăm tr&ecirc;n gi&aacute; trị của 1 sản phẩm.</p>\r\n\r\n<p>Ri&ecirc;ng với c&aacute;c trường hợp kh&aacute;ch h&agrave;ng mua nhiều hơn từ 2 sản phẩm trở l&ecirc;n v&agrave; tất cả c&aacute;c mặt h&agrave;ng đ&atilde; mua đều nằm trong danh s&aacute;ch &aacute;p dụng th&igrave; số điểm t&iacute;ch vẫn t&iacute;nh tr&ecirc;n gi&aacute; trị của từng sản phẩm.</p>\r\n\r\n<p><strong>V&iacute; dụ:</strong>&nbsp;Khi kh&aacute;ch mua cả điện thoại v&agrave; đồng hồ th&igrave; số điểm sẽ t&iacute;ch l&agrave; 0.2% gi&aacute; trị điện thoại v&agrave; 0.5% gi&aacute; trị đồng hồ.</p>\r\n\r\n<table align=\"center\" border=\"2\" cellpadding=\"2\" cellspacing=\"10\" style=\"width:900px\">\r\n	<tbody>\r\n		<tr>\r\n			<td><strong>ng&agrave;nh h&agrave;ng</strong></td>\r\n			<td><strong>phần trăm t&iacute;ch điểm</strong></td>\r\n		</tr>\r\n		<tr>\r\n			<td><a href=\"https://www.dienmayxanh.com/dien-thoai\" target=\"_blank\">Điện thoại</a>,&nbsp;<a href=\"https://www.dienmayxanh.com/may-tinh-bang\" target=\"_blank\">tablet</a>,&nbsp;<a href=\"https://www.dienmayxanh.com/laptop-ldp\" target=\"_blank\">laptop</a>&nbsp;(ngoại trừ h&atilde;ng Apple)</td>\r\n			<td>0,2%</td>\r\n		</tr>\r\n		<tr>\r\n			<td><a href=\"https://www.dienmayxanh.com/dien-thoai-apple-iphone\" target=\"_blank\">Điện thoại</a>,&nbsp;<a href=\"https://www.dienmayxanh.com/may-tinh-bang-apple-ipad\" target=\"_blank\">tablet</a>,&nbsp;<a href=\"https://www.dienmayxanh.com/laptop-apple-macbook\" target=\"_blank\">laptop</a>&nbsp;của Apple</td>\r\n			<td>0,1%</td>\r\n		</tr>\r\n		<tr>\r\n			<td><a href=\"https://www.dienmayxanh.com/phu-kien\" target=\"_blank\">hụ kiện</a>,&nbsp;<a href=\"https://www.dienmayxanh.com/dong-ho\" target=\"_blank\">đồng hồ</a>, c&aacute;c nh&oacute;m ng&agrave;nh kh&aacute;c (ngoại trừ h&atilde;ng Apple)</td>\r\n			<td>0,5%</td>\r\n		</tr>\r\n	</tbody>\r\n</table>\r\n\r\n<p><strong>Lưu &yacute;:</strong>&nbsp;Đối với những mặt h&agrave;ng c&oacute; k&egrave;m theo chương tr&igrave;nh khuyến m&atilde;i th&igrave; khi t&iacute;ch điểm, số điểm sẽ được t&iacute;nh tr&ecirc;n gi&aacute; đ&atilde; giảm.</p>', 'Đây là một ứng dụng được phát triển bởi đội ngũ nhân viên MWG, nhằm mang đến trải nghiệm tốt nhất đối với khách hàng khi sử dụng dịch vụ của MWG.', '20240629172042.jpg', 'post', '2024-06-29 11:29:55', '2024-06-29 11:29:55', 1, NULL, 1),
(23, NULL, 'Giới thiệu', 'gioi-thieu', '<p>Ch&agrave;o mừng bạn đến với của ch&uacute;ng t&ocirc;i, điểm đến h&agrave;ng đầu d&agrave;nh cho tất cả những ai y&ecirc;u th&iacute;ch v&agrave; đam m&ecirc; c&ocirc;ng nghệ. Tại đ&acirc;y, ch&uacute;ng t&ocirc;i cung cấp một trải nghiệm mua sắm trực tuyến ho&agrave;n hảo, nơi bạn c&oacute; thể t&igrave;m thấy mọi thứ từ c&aacute;c thiết bị điện tử, phụ kiện c&ocirc;ng nghệ đến c&aacute;c sản phẩm mới nhất trong ng&agrave;nh c&ocirc;ng nghiệp c&ocirc;ng nghệ.</p>\r\n\r\n<p>&nbsp;</p>\r\n\r\n<p><strong>1. Đa Dạng Sản Phẩm</strong></p>\r\n\r\n<p>Trang web của ch&uacute;ng t&ocirc;i tự h&agrave;o cung cấp một danh mục sản phẩm đa dạng v&agrave; phong ph&uacute;. Bạn c&oacute; thể dễ d&agrave;ng t&igrave;m thấy c&aacute;c d&ograve;ng sản phẩm như điện thoại di động, m&aacute;y t&iacute;nh bảng, laptop, phụ kiện gaming, thiết bị nh&agrave; th&ocirc;ng minh v&agrave; nhiều hơn nữa. Mỗi sản phẩm được ch&uacute;ng t&ocirc;i chọn lọc kỹ lưỡng từ những thương hiệu uy t&iacute;n v&agrave; nổi tiếng tr&ecirc;n thế giới như Apple, Samsung, Sony, Dell, HP, v&agrave; nhiều thương hiệu kh&aacute;c.</p>\r\n\r\n<p><strong>2. Chất Lượng Đảm Bảo</strong></p>\r\n\r\n<p>Ch&uacute;ng t&ocirc;i cam kết cung cấp sản phẩm ch&iacute;nh h&atilde;ng với chất lượng tốt nhất. Mỗi sản phẩm được kiểm tra kỹ lưỡng trước khi đến tay kh&aacute;ch h&agrave;ng. B&ecirc;n cạnh đ&oacute;, ch&uacute;ng t&ocirc;i lu&ocirc;n cung cấp th&ocirc;ng tin chi tiết về sản phẩm, gi&uacute;p bạn dễ d&agrave;ng so s&aacute;nh v&agrave; lựa chọn sản phẩm ph&ugrave; hợp nhất với nhu cầu của m&igrave;nh.</p>\r\n\r\n<p><strong>3. Dịch Vụ Kh&aacute;ch H&agrave;ng Chuy&ecirc;n Nghiệp</strong></p>\r\n\r\n<p>Đội ngũ chăm s&oacute;c kh&aacute;ch h&agrave;ng của ch&uacute;ng t&ocirc;i lu&ocirc;n sẵn s&agrave;ng hỗ trợ bạn 24/7. Với phương ch&acirc;m &quot;Kh&aacute;ch h&agrave;ng l&agrave; thượng đế&quot;, ch&uacute;ng t&ocirc;i lu&ocirc;n lắng nghe v&agrave; giải đ&aacute;p mọi thắc mắc của bạn một c&aacute;ch nhanh ch&oacute;ng v&agrave; tận t&acirc;m. Bạn c&oacute; thể li&ecirc;n hệ với ch&uacute;ng t&ocirc;i qua nhiều k&ecirc;nh như chat trực tuyến, email, hoặc hotline.</p>\r\n\r\n<p><strong>4. Ưu Đ&atilde;i Hấp Dẫn</strong></p>\r\n\r\n<p>Ch&uacute;ng t&ocirc;i thường xuy&ecirc;n tổ chức c&aacute;c chương tr&igrave;nh khuyến m&atilde;i v&agrave; giảm gi&aacute; đặc biệt. Bạn c&oacute; thể theo d&otilde;i trang web v&agrave; đăng k&yacute; nhận bản tin để kh&ocirc;ng bỏ lỡ bất kỳ ưu đ&atilde;i n&agrave;o. Những kh&aacute;ch h&agrave;ng th&acirc;n thiết c&ograve;n nhận được nhiều ưu đ&atilde;i đặc biệt v&agrave; qu&agrave; tặng hấp dẫn.</p>\r\n\r\n<p><strong>5. Giao H&agrave;ng Nhanh Ch&oacute;ng</strong></p>\r\n\r\n<p>Ch&uacute;ng t&ocirc;i hiểu rằng thời gian của bạn rất qu&yacute; b&aacute;u, v&igrave; vậy ch&uacute;ng t&ocirc;i cung cấp dịch vụ giao h&agrave;ng nhanh ch&oacute;ng v&agrave; tiện lợi. Bạn c&oacute; thể lựa chọn nhiều h&igrave;nh thức giao h&agrave;ng kh&aacute;c nhau để ph&ugrave; hợp với nhu cầu v&agrave; thời gian của m&igrave;nh.</p>\r\n\r\n<p><strong>6. Ch&iacute;nh S&aacute;ch Đổi Trả Linh Hoạt</strong></p>\r\n\r\n<p>Để mang lại sự y&ecirc;n t&acirc;m cho kh&aacute;ch h&agrave;ng, ch&uacute;ng t&ocirc;i c&oacute; ch&iacute;nh s&aacute;ch đổi trả linh hoạt. Nếu sản phẩm bạn nhận được kh&ocirc;ng đ&uacute;ng như mong đợi hoặc c&oacute; lỗi kỹ thuật, bạn c&oacute; thể dễ d&agrave;ng đổi trả trong v&ograve;ng 7 ng&agrave;y kể từ ng&agrave;y nhận h&agrave;ng.</p>\r\n\r\n<table border=\"2\">\r\n	<tbody>\r\n		<tr>\r\n			<td><strong>Điều Kiện</strong></td>\r\n			<td><strong>Chi Tiết</strong></td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>Thời Gian Đổi Trả</strong></td>\r\n			<td>Trong v&ograve;ng 7 ng&agrave;y kể từ ng&agrave;y nhận h&agrave;ng.</td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>Điều Kiện Sản Phẩm</strong></td>\r\n			<td>Sản phẩm phải c&ograve;n nguy&ecirc;n vẹn, chưa qua sử dụng, v&agrave; đầy đủ phụ kiện, bao b&igrave;.</td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>Chi Ph&iacute; Đổi Trả</strong></td>\r\n			<td>Miễn ph&iacute; đổi trả nếu sản phẩm c&oacute; lỗi do nh&agrave; sản xuất. Kh&aacute;ch h&agrave;ng chịu ph&iacute; vận chuyển trong trường hợp đổi trả do l&yacute; do c&aacute; nh&acirc;n.</td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>Quy Tr&igrave;nh Đổi Trả</strong></td>\r\n			<td>Li&ecirc;n hệ với bộ phận chăm s&oacute;c kh&aacute;ch h&agrave;ng để được hướng dẫn chi tiết về quy tr&igrave;nh đổi trả.</td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>H&igrave;nh Thức Ho&agrave;n Tiền</strong></td>\r\n			<td>Ho&agrave;n tiền qua t&agrave;i khoản ng&acirc;n h&agrave;ng hoặc đổi sản phẩm mới t&ugrave;y theo lựa chọn của kh&aacute;ch h&agrave;ng.</td>\r\n		</tr>\r\n	</tbody>\r\n</table>\r\n\r\n<p><strong>7. Ch&iacute;nh S&aacute;ch Bảo H&agrave;nh</strong></p>\r\n\r\n<p>Ch&uacute;ng t&ocirc;i cam kết cung cấp ch&iacute;nh s&aacute;ch bảo h&agrave;nh minh bạch v&agrave; uy t&iacute;n. Tất cả c&aacute;c sản phẩm mua từ [T&ecirc;n Trang Web] đều đi k&egrave;m với bảo h&agrave;nh ch&iacute;nh h&atilde;ng từ nh&agrave; sản xuất. Trong thời gian bảo h&agrave;nh, nếu sản phẩm gặp bất kỳ vấn đề kỹ thuật n&agrave;o, bạn c&oacute; thể li&ecirc;n hệ với ch&uacute;ng t&ocirc;i hoặc c&aacute;c trung t&acirc;m bảo h&agrave;nh ủy quyền để được hỗ trợ sửa chữa hoặc thay thế. Ch&uacute;ng t&ocirc;i lu&ocirc;n sẵn s&agrave;ng hỗ trợ v&agrave; đảm bảo rằng bạn sẽ nhận được dịch vụ bảo h&agrave;nh tốt nhất.</p>\r\n\r\n<table border=\"2\">\r\n	<tbody>\r\n		<tr>\r\n			<td><strong>Điều Kiện</strong></td>\r\n			<td><strong>Chi Tiết</strong></td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>Thời Gian Bảo H&agrave;nh</strong></td>\r\n			<td>T&ugrave;y thuộc v&agrave;o từng sản phẩm, th&ocirc;ng thường từ 6 th&aacute;ng đến 2 năm.</td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>Phạm Vi Bảo H&agrave;nh</strong></td>\r\n			<td>Bảo h&agrave;nh c&aacute;c lỗi kỹ thuật do nh&agrave; sản xuất. Kh&ocirc;ng bảo h&agrave;nh c&aacute;c lỗi do người sử dụng g&acirc;y ra như va đập, rơi vỡ, hoặc sử dụng sai hướng dẫn.</td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>Dịch Vụ Bảo H&agrave;nh</strong></td>\r\n			<td>Sửa chữa hoặc thay thế miễn ph&iacute; c&aacute;c linh kiện hỏng h&oacute;c trong thời gian bảo h&agrave;nh.</td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>Quy Tr&igrave;nh Bảo H&agrave;nh</strong></td>\r\n			<td>Li&ecirc;n hệ với bộ phận chăm s&oacute;c kh&aacute;ch h&agrave;ng hoặc c&aacute;c trung t&acirc;m bảo h&agrave;nh ủy quyền để được hướng dẫn chi tiết về quy tr&igrave;nh bảo h&agrave;nh.</td>\r\n		</tr>\r\n		<tr>\r\n			<td><strong>Thời Gian Xử L&yacute; Bảo H&agrave;nh</strong></td>\r\n			<td>Thời gian xử l&yacute; bảo h&agrave;nh thường từ 7-15 ng&agrave;y l&agrave;m việc, t&ugrave;y thuộc v&agrave;o mức độ phức tạp của lỗi kỹ thuật.</td>\r\n		</tr>\r\n	</tbody>\r\n</table>\r\n\r\n<p>&nbsp;</p>', 'Giới thiệu về trang web của chúng tôi.', '20240629175147.jpg', 'page', '2024-06-29 11:26:15', '2024-06-29 11:26:15', 1, NULL, 1),
(24, 3, 'Giải mã sức Samsung Galaxy M55 5G', 'giai-ma-suc-samsung-galaxy-m55-5g', '<h2><strong>1. Camera cao cấp 50MP, chống rung OIS</strong></h2>\r\n\r\n<p>Điểm ấn tượng đầu ti&ecirc;n l&agrave; hệ thống camera 50MP, hỗ trợ chống rung OIS, Galaxy M55 5G trở th&agrave;nh người bạn nhiếp ảnh tuyệt vời cho người d&ugrave;ng. Sự c&oacute; mặt của cảm biến độ ph&acirc;n giải lớn mang tới chất lượng ảnh tốt, sắc n&eacute;t v&agrave; ch&acirc;n thực. Đối với OIS c&oacute; khả năng cố định khung h&igrave;nh, hạn chế ảnh nh&ograve;e hay mờ, ảnh v&agrave; video sẽ mượt v&agrave; r&otilde; n&eacute;t hơn. Nhờ vậy, d&ugrave; bạn c&oacute; đang di chuyển th&igrave; tấm ảnh cũng kh&ocirc;ng bị ảnh hưởng qu&aacute; nhiều. Camera ph&ugrave; hợp với c&aacute;c bạn l&agrave;m TikToker cần một thiết bị quay video mượt m&agrave; v&agrave; chất lượng.</p>\r\n\r\n<h2><strong>1. Camera cao cấp 50MP, chống rung OIS</strong></h2>\r\n\r\n<p>Điểm ấn tượng đầu ti&ecirc;n l&agrave; hệ thống camera 50MP, hỗ trợ chống rung OIS, Galaxy M55 5G trở th&agrave;nh người bạn nhiếp ảnh tuyệt vời cho người d&ugrave;ng. Sự c&oacute; mặt của cảm biến độ ph&acirc;n giải lớn mang tới chất lượng ảnh tốt, sắc n&eacute;t v&agrave; ch&acirc;n thực. Đối với OIS c&oacute; khả năng cố định khung h&igrave;nh, hạn chế ảnh nh&ograve;e hay mờ, ảnh v&agrave; video sẽ mượt v&agrave; r&otilde; n&eacute;t hơn. Nhờ vậy, d&ugrave; bạn c&oacute; đang di chuyển th&igrave; tấm ảnh cũng kh&ocirc;ng bị ảnh hưởng qu&aacute; nhiều. Camera ph&ugrave; hợp với c&aacute;c bạn l&agrave;m TikToker cần một thiết bị quay video mượt m&agrave; v&agrave; chất lượng.</p>\r\n\r\n<p><img alt=\"Giải mã sức hút Samsung Galaxy M55 5G (ảnh 1)\" src=\"https://cdn2.fptshop.com.vn/unsafe/Uploads/images/tin-tuc/188571/Originals/Giai-ma-suc-hut-samsung-galaxy-m55-5g-3.jpg\" /></p>\r\n\r\n<p>Galaxy M55 5G l&agrave; chiếc điện thoại cận cao cấp mới của Samsung trong năm nay. Điện thoại c&oacute; điểm nhấn về cấu h&igrave;nh mạnh cũng như hệ thống camera 50MP chất lượng v&agrave; giải tr&iacute; cực đỉnh bởi m&agrave;n h&igrave;nh Super AMOLED 120Hz.</p>\r\n\r\n<h2><strong>2. Hiệu năng mạnh Snapdragon 7 Gen 1, 8GB RAM</strong></h2>\r\n\r\n<p>Được xem l&agrave; &ldquo;chiến binh&rdquo; trong ph&acirc;n kh&uacute;c, Galaxy M55 5G sử dụng chip&nbsp;<a href=\"https://fptshop.com.vn/tin-tuc/danh-gia/tim-hieu-snapdragon-7-gen-1-146162\">Snapdragon 7 Gen 1</a>&nbsp;tiến tr&igrave;nh 4nm, xung nhịp đến 2.4GHz, đồ họa nhanh hơn 20%, đi k&egrave;m 8GB RAM, cho ph&eacute;p đa nhiệm mạnh mẽ, &quot;chiến&quot; game thoải m&aacute;i.&nbsp;</p>\r\n\r\n<p><img alt=\"Giải mã sức hút Samsung Galaxy M55 5G (ảnh 2)\" src=\"https://cdn2.fptshop.com.vn/unsafe/Uploads/images/tin-tuc/188571/Originals/Giai-ma-suc-hut-samsung-galaxy-m55-5g.jpg\" /><br />\r\n&nbsp;</p>\r\n\r\n<p>Ưu điểm của bộ vi xử l&yacute; Snapdragon 7 Gen 1 kh&ocirc;ng chỉ dừng lại ở sức mạnh m&agrave; c&ograve;n l&agrave; khả năng tiết kiệm điện năng hiệu quả, k&eacute;o d&agrave;i thời lượng sử dụng với 4 l&otilde;i tiết kiệm năng lượng Cortex-A510 xung nhịp 1.8GHz. Nhờ vậy, m&aacute;y vừa xử l&yacute; nhanh ch&oacute;ng đa t&aacute;c vụ từ cơ bản đến phức tạp, vừa gi&uacute;p tiết kiệm pin tốt. Bạn c&oacute; thể giải tr&iacute; thỏa th&iacute;ch m&agrave; kh&ocirc;ng lắng lo hiệu năng v&agrave; pin sẽ l&agrave; r&agrave;o cản.</p>\r\n\r\n<h2><strong>3. Pin lớn, sạc nhanh 45W</strong></h2>\r\n\r\n<p>D&ograve;ng Galaxy M lu&ocirc;n được đ&aacute;nh gi&aacute; cao về dung lượng pin v&agrave; Galaxy M55 5G cũng kh&ocirc;ng ngoại lệ. Điện thoại c&oacute; vi&ecirc;n pin 5.000mAh, đ&aacute;p ứng tốt nhu cầu sử dụng cả ng&agrave;y d&agrave;i hay thậm ch&iacute; đến 2 ng&agrave;y, ph&ugrave; hợp cho người d&ugrave;ng học sinh, sinh vi&ecirc;n hay thường xuy&ecirc;n di chuyển, kh&ocirc;ng lo gi&aacute;n đoạn giữa ng&agrave;y bất tiện.&nbsp;</p>\r\n\r\n<p><img alt=\"Giải mã sức hút Samsung Galaxy M55 5G (ảnh 3)\" src=\"https://cdn2.fptshop.com.vn/unsafe/Uploads/images/tin-tuc/188571/Originals/Giai-ma-suc-hut-samsung-galaxy-m55-5g-4.jpg\" /></p>\r\n\r\n<p>Chưa hết, Galaxy M55 5G c&ograve;n c&oacute; một điểm rất ấn tượng so với thế hệ cũ v&agrave; c&aacute;c sản phẩm tầm trung, cận cao cấp kh&aacute;c của Samsung l&agrave; hỗ trợ c&ocirc;ng nghệ sạc nhanh 45W. Một đặc quyền ri&ecirc;ng d&agrave;nh cho Galaxy M55 5G gi&uacute;p bạn lấy lại năng lượng nhanh ch&oacute;ng, tiết kiệm thời gian sạc v&agrave; kh&ocirc;ng chờ đợi qu&aacute; l&acirc;u.</p>', 'Giải mã sức hút Samsung Galaxy M55 5G trong tầm giá 11 triệu đồng: Camera chất lượng, cấu hình \"xịn sò\", rất đáng để sở hữu', '20241007020930.jpeg', 'post', '2024-06-30 22:03:35', '2024-10-06 19:12:39', 1, 1, 1),
(28, 3, 'Galaxy S25 Series sẽ không được trang bị màn hình tốt nhất của Samsung', 'galaxy-s25-series-se-khong-duoc-trang-bi-man-hinh-tot-nhat-cua-samsung', 'Với mỗi tin đồn và thông tin bị rò rỉ mới đã khiến bức tranh trở nên rõ ràng hơn về những gì được mong đợi từ dòng Samsung Galaxy S25. Mới đây, một nguồn tin mới khẳng định cả ba mẫu smartphone trong dòng Galaxy S25 sẽ được trang bị công nghệ màn hình cũ của Samsung. \r\nMột báo cáo đến từ Electronic Newspaper của Hàn Quốc khẳng định Samsung đang cố gắng tiết kiệm tiền cho việc phát triển dòng Galaxy S25. Có vẻ như màn hình là một trong những mục tiêu chính của Samsung đưa ra để cắt giảm chi phí sản xuất.\r\n\r\nBằng cách sử dụng tùy chọn màn hình cũ hơn, Samsung sẽ có thể giảm chi phí sản xuất. Chúng ta phải chờ đợi cho đến khi thiết bị được ​​ra mắt vào năm sau để biết được Samsung sẽ trang bị loại màn hình nào cho dòng Galaxy S25.', 'Galaxy S25 Series sẽ không được trang bị tấm nền OLED tốt nhất của Samsung', '20241028040416.jpg', 'post', '2024-10-27 21:04:16', '2024-10-27 21:04:16', 1, NULL, 1),
(29, 3, 'So sánh Apple Watch Ultra 2 với Apple Watch Ultra: Nâng cấp đáng giá hay không?', 'so-sanh-apple-watch-ultra-2-voi-apple-watch-ultra-nang-cap-dang-gia-hay-khong', 'Apple Watch Ultra 2 ra mắt với nhiều cải tiến đáng chú ý so với phiên bản tiền nhiệm. Vậy những nâng cấp này có đủ sức thuyết phục người dùng \"xuống tiền\" hay không? Hãy cùng so sánh Apple Watch Ultra 2 với Apple Watch Ultra để có câu trả lời.\r\nThiết kế và Màn hình: Sự tương đồng đến bất ngờ\r\nVề thiết kế, Apple Watch Ultra 2 gần như giống hệt Apple Watch Ultra. Vẫn là lớp vỏ titanium 49mm bền bỉ, núm Digital Crown cỡ lớn và nút Action Button đặc trưng. Điểm khác biệt duy nhất có lẽ nằm ở tùy chọn màu sắc mới - màu titan tự nhiên với lớp hoàn thiện sáng bóng hơn.\r\n\r\nMàn hình cũng là một điểm tương đồng giữa hai thế hệ. Cả hai đều sở hữu màn hình Retina LTPO OLED luôn bật với kích thước 1.92 inch, độ phân giải 502 x 410 pixel. Tuy nhiên, Apple Watch Ultra 2 được nâng cấp đáng kể về độ sáng, đạt mức tối đa 3.000 nit, gấp đôi so với 2.000 nits trên Apple Watch Ultra. Điều này giúp khả năng hiển thị ngoài trời nắng gắt được cải thiện rõ rệt.\r\nHiệu năng và phần mềm: Nâng cấp đáng kể\r\nApple Watch Ultra 2 được trang bị chip S9 SiP mới, mạnh mẽ hơn chip S8 trên Apple Watch Ultra. Kết hợp với Neural Engine 4 lõi mới và hệ điều hành watchOS 10, hiệu năng xử lý được cải thiện đáng kể, đặc biệt là các tác vụ liên quan đến máy học. \r\n\r\nwatchOS 10 cũng mang đến nhiều tính năng mới hữu ích như Smart Stack, giúp truy cập nhanh các widget quan trọng; giao diện ứng dụng được thiết kế lại trực quan hơn; và các cải tiến cho ứng dụng luyện tập.', 'So sánh Apple Watch Ultra 2 với Apple Watch Ultra: Nâng cấp đáng giá hay không?', '20241028040630.jpg', 'post', '2024-10-27 21:06:30', '2024-10-27 21:06:30', 1, NULL, 1),
(30, 8, 'OnePlus 13 ra mắt', 'oneplus-13-ra-mat', 'Mới đây, OnePlus đã chính thức trình làng thế hệ flagship mới nhất của mình - OnePlus 13. Mẫu smartphone này được mang trên mình nhiều nâng cấp mới, cùng với đó là bộ vi xử lý mạnh mẽ và hệ thống camera Hasselblad đỉnh cao. \r\nThông số cấu hình OnePlus 13\r\nVề thiết kế, OnePlus 13 được hãng làm phẳng ở cả hai mặt, cùng với đó là khung viền được gia công bằng vật liệu nhôm. Hơn nữa, mẫu smartphone này đã thay đổi ngôn ngữ thiết kế với cụm camera to tròn đặc lệch cùng logo Hasselblad đặt bên phải cùng logo OnePlus đặt ở chính giữa tạo nên sự tinh tế và các góc được bo tròn mềm mại.\r\n\r\nKhả năng hiển thị của OnePlus 13 cũng khá ấn tượng, máy được trang bị màn hình OLED BOE X2 8T LTPO kích thước 6.82 inch, độ phân giải 2K và tần số quét 120 Hz. Cùng với đó, tấm nền trên OnePlus 13 còn cho ra độ sáng tối đa 4.500 nits và đạt chứng nhận DisplayMate A++. \r\n\r\nSức mạnh của OnePlus 13 còn đến từ vi xử lý Snapdragon 8 Elite, kết hợp với đó là dung lượng RAM LPPDR5X lên đến 24 GB và dung lượng bộ nhớ trong UFS 4.0 lên đến 1 TB. Cung cấp năng lượng cho OnePlus 13 là viên pin 6.000 mAh, hỗ trợ sạc nhanh có dây 100 W và sạc nhanh không dây 50 W. \r\n\r\nOnePlus 13 được trang bị hệ thống camera mạnh mẽ với camera chính Sony LYT-808 50 MP được tinh chỉnh bởi Hasselblad, kết hợp với đó là camera góc siêu rộng 50 MP và camera tele 50 MP hỗ trợ zoom quang học 3X. Ở mặt trước, OnePlus 13 được trang bị camera selfie 32 MP. \r\n\r\nGiá bán OnePlus 13\r\nOnePlus 13 mang đến cho người dùng 3 tùy chọn màu sắc cực kỳ sang trọng, gồm: White Dawn, Blue Moment và Obsidian Secret. Cả ba màu sắc này đều được hãng sử nhiều công nghệ để hoàn thiện như Silk Glass ở trên màu White Dawn, BabySkin ở trên màu Blue Moment và kính vân gỗ Ebony trên màu Obsidian Secret. \r\n\r\nCùng với đó, dưới đây là danh sách giá bán và các tùy chọn cấu hình của OnePlus 13 tại thị trường Trung Quốc: \r\n\r\nPhiên bản RAM 12 GB + ROM 256 GB có giá 4.499 CNY (khoảng 15.9 triệu đồng).\r\nPhiên bản RAM 12 GB + ROM 512 GB có giá 4.899 CNY (khoảng 17.4 triệu đồng). \r\nPhiên bản RAM 16 GB + ROM 512 GB có giá 5.299 CNY (khoảng 18.8 triệu đồng). \r\nPhiên bản RAM 24 GB + ROM 1 TB có giá 5.999 CNY (khoảng 21.3 triệu đồng).\r\nBạn có suy nghĩ gì về OnePlus 13?', 'OnePlus 13 ra mắt: Thiết kế mới sang trọng, camera Hasselblad 50MP, chip Snapdragon 8 Elite và nhiều nâng cấp mới', '20241031134352.jpg', 'post', '2024-10-31 06:43:52', '2024-10-31 06:43:52', 1, NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_product`
--

CREATE TABLE `cdtt_product` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `category_id` int(10) UNSIGNED NOT NULL,
  `brand_id` int(10) UNSIGNED NOT NULL,
  `name` varchar(1000) NOT NULL,
  `slug` varchar(1000) NOT NULL,
  `detail` text NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `image` varchar(1000) NOT NULL,
  `price` double NOT NULL,
  `pricesale` double DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int(10) UNSIGNED NOT NULL,
  `updated_by` int(10) UNSIGNED DEFAULT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_product`
--

INSERT INTO `cdtt_product` (`id`, `category_id`, `brand_id`, `name`, `slug`, `detail`, `description`, `image`, `price`, `pricesale`, `created_at`, `updated_at`, `created_by`, `updated_by`, `status`) VALUES
(1015, 56, 1, 'Laptop ASUS TUF F15', 'laptop-asus-tuf-f15', 'Bên cạnh việc cung cấp khả năng chiến đấu tuyệt vời với kho game lớn ấn tượng từ những tựa trò chơi nhập vai, đua xe FPS như LOL, CS:GO, Valorant.', 'LaptopASUS TUF F15 mang dáng dấp kiểu mẫu từ những chiếc laptop gaming.', '20240617131538.jpg', 13890000, NULL, '2024-06-17 18:44:44', '2024-06-25 20:59:06', 1, 1, 1),
(1016, 64, 17, 'SAMSUNG galaxy A54', 'samsung-galaxy-a54', 'Samsung Galaxy A55 5G (8GB/256GB) một thành viên mới nhất trong dòng sản phẩm A series, hứa hẹn mang đến nhiều tính năng tiên tiến cùng với kết nối 5G tốc độ cao. Sản phẩm được trình làng với mục tiêu cung cấp một chiếc điện thoại thông minh cao cấp, đa chức năng nhưng với mức giá phải chăng.', 'Với thiết kế hiện đại, Galaxy A55 5G tự hào sở hữu một tạo hình vuông vức, tinh tế.', '20240617132214.webp', 853000000, 8000000, '2024-06-17 06:44:46', '2024-06-17 06:44:46', 1, NULL, 1),
(1017, 64, 19, 'OPPO RENO 11', 'oppo-reno-11', 'OPPO Reno11 F 5G là một chiếc điện thoại tầm trung mới được OPPO ra mắt trong thời gian gần đây. Máy sở hữu nhiều ưu điểm nổi bật như thiết kế trẻ trung, màn hình đẹp, hiệu năng mạnh mẽ nhờ chip Dimensity 7050 5G, hứa hẹn mang đến trải nghiệm tốt khi sử dụng.', 'Thiết kế của điện thoại này được tạo hình vuông vức hiện đại, đem đến một vẻ đẹp đầy trẻ trung.', '20240617134249.webp', 7999900, 7000000, '2024-06-17 06:42:49', '2024-06-17 06:42:49', 1, NULL, 1),
(1018, 64, 18, 'IPHONE 15', 'iphone-15', 'Trong khi sức hút đến từ bộ 4 phiên bản iPhone 12 vẫn chưa nguội đi, thì hãng điện thoại Apple đã mang đến cho người dùng một siêu phẩm mới iPhone 15 series với nhiều cải tiến thú vị sẽ mang lại những trải nghiệm hấp dẫn nhất cho người dùng.', 'Hiệu năng vượt trội nhờ chip Apple A15 Bionic.', '20240617134432.webp', 15000000, 13000000, '2024-06-17 06:44:32', '2024-06-17 06:44:32', 1, NULL, 1),
(1019, 65, 18, 'Apple Watch SE 2023', 'apple-watch-se-2023', 'Apple Watch SE 2023 GPS 40mm viền nhôm dây thể thao là chiếc smartwatch có mức giá dễ tiếp cận nhất nhà Táo, là lựa chọn tối ưu ngân sách cho người dùng nhưng vẫn đảm bảo một thiết kế đẹp mắt, hỗ trợ đa dạng tính năng cũng như tiện ích hằng ngày.', 'Thiết kế quen thuộc với độ nhận diện cao.', '20240617135117.webp', 90000000, 87000000, '2024-06-17 06:51:17', '2024-06-25 20:59:18', 1, 1, 1),
(1020, 65, 18, 'Apple Watch Series 9', 'apple-watch-series-9', 'Tại sự kiện Wonderlust, Apple đã chính thức ra mắt Apple Watch Series 9 - thế hệ đồng hồ thông minh mới của hãng trong năm 2023. Trong đó, Apple Watch Series 9 GPS 41mm viền nhôm dây vải đã thu hút được nhiều sự chú ý với ngoại hình thời thượng, hiệu năng mạnh mẽ, sẵn sàng đồng hành cùng người dùng trong nhiều hoạt động.', 'Các chi tiết được chế tác tỉ mỉ, vẻ ngoài sang trọng.', '20240617135130.jpg', 5800000, 55000000, '2024-06-17 06:51:30', '2024-06-25 20:59:20', 1, 1, 1),
(1021, 56, 18, 'Laptop Apple MacBook Air 13', 'laptop-apple-macbook-air-13', 'MacBook Air M2 2022 một lần nữa đã khẳng định vị thế hàng đầu của Apple trong phân khúc laptop cao cấp - sang trọng vào giữa năm 2022 khi sở hữu phong cách thiết kế thời thượng, đẳng cấp cùng sức mạnh bộc phá đến từ bộ vi xử lý Apple M2 mạnh mẽ.', 'Khám phá nguồn sức mạnh từ dòng chip thế hệ mới.', '20240617135411.webp', 1999000, 1900000, '2024-06-17 06:54:11', '2024-06-25 20:59:38', 1, 1, 1),
(1022, 66, 17, 'Máy tính bảng Samsung Galaxy Tab A9+', 'may-tinh-bang-samsung-galaxy-tab-a9', 'Với giá cả phải chăng, Samsung Galaxy Tab A9+ 5G là một sản phẩm máy tính bảng của Samsung dành cho người dùng muốn sở hữu một thiết bị giải trí cơ bản với màn hình rộng và khả năng kết nối mạng toàn diện để truy cập internet bất kỳ lúc nào và ở bất kỳ đâu.', 'Thiết kế của Galaxy Tab A9+ 5G đem đến một sự tươi mới và tinh tế.', '20240617135543.webp', 1500000, 1300000, '2024-06-23 20:21:43', '2024-06-25 20:58:56', 1, 1, 1),
(1023, 64, 17, 'Samsung Galaxy Z Fold5', 'samsung-galaxy-z-fold5', 'Samsung Galaxy Z Fold5 5G 512GB cũng đã chính thức ra mắt vào tháng 07/2023 với sự bứt phá mạnh mẽ, mở ra những trải nghiệm linh hoạt mới mẻ thông qua khả năng gập độc đáo, màn hình lớn sắc nét, hiệu năng vượt trội cho khả năng đa nhiệm tối ưu và cùng nhiều tính năng hấp dẫn khác đáp ứng những yêu cầu cao hơn từ người dùng.', 'Galaxy Z Fold5, chiếc smartphone lớn nổi bật của Samsung, sau khi đã ra mắt thành công.', '20240617135752.webp', 3500000, 3400000, '2024-06-17 06:58:01', '2024-06-25 20:59:34', 1, 1, 1),
(1024, 64, 17, 'Samsung Galaxy Z Flip5', 'samsung-galaxy-z-flip5', 'Samsung Galaxy Z Flip5 5G 512GB cũng đã chính thức ra mắt, được xem là một kiệt tác về thiết kế độc đáo đầy cuốn hút, thiết bị này hứa hẹn sẽ làm say đắm lòng người bởi vẻ ngoài vuông vắn tinh tế, khả năng gập độc đáo theo hướng dọc cùng cấu hình mạnh mẽ hàng đầu trong ngành.', 'Galaxy Z Flip5, một sản phẩm chỉ vừa được ra mắt, đã nhanh chóng được trang bị những tính năng AI.', '20240617135947.webp', 28900000, NULL, '2024-06-17 18:44:31', '2024-06-25 20:59:12', 1, 1, 1),
(1028, 64, 18, 'iPhone 15 Pro Max', 'iphone-15-pro-max', 'iPhone 15 Pro Max là chiếc smartphone cao cấp nhất của nhà iphone, sở hữu cấu hình không tưởng với con chip khủng được Qualcomm tối ưu riêng cho dòng Galaxy và camera lên đến 200 MP, xứng danh là chiếc flagship Android được mong đợi nhất trong năm 2023.\r\nTích hợp các tính năng AI.', 'iPhone 15 Pro Max đã gây ấn tượng mạnh mẽ khi ra mắt với thiết kế đột phá và hiệu suất vượt trội.', '20240924025026.jpeg', 4500000, 4200000, '2024-09-23 17:47:57', '2024-09-25 17:36:47', 1, 1, 1),
(1029, 64, 17, 'Samsung Galaxy S23 Ultra 5G', 'samsung-galaxy-s23-ultra-5g', 'Samsung Galaxy S23 Ultra 5G 256GB là chiếc smartphone cao cấp nhất của nhà Samsung, sở hữu cấu hình không tưởng với con chip khủng được Qualcomm tối ưu riêng cho dòng Galaxy và camera lên đến 200 MP, xứng danh là chiếc flagship Android được mong đợi nhất trong năm 2023.', 'Galaxy S23 Ultra đã gây ấn tượng mạnh mẽ khi ra mắt với thiết kế đột phá và hiệu suất vượt trội.', '20240618054636.jpg', 50000000, 49000000, '2024-06-17 22:46:36', '2024-06-25 20:59:09', 1, 1, 1),
(1030, 56, 1, 'Laptop Asus TUF Gaming F15 FX507ZC4', 'laptop-asus-tuf-gaming-f15-fx507zc4', 'Laptop Asus TUF Gaming F15 FX507ZC4 i5 12500H (HN229W) với cấu trúc mạnh mẽ, hiệu năng vượt trội cùng mức giá hoàn toàn ưu đãi tại Thế Giới Di Động. Đây chính xác là mẫu laptop gaming được thiết kế dành riêng cho những anh em đam mê thể thao điện tử, đáp ứng đầy đủ đến cả những công việc thiết kế, sáng tạo.', 'CPU Intel Core i5 12500H dòng H hiệu năng cao cung cấp khả năng vận hành đa nhiệm tuyệt vời.', '20240624031710.webp', 21000000, NULL, '2024-06-23 20:17:11', '2024-06-25 20:59:03', 1, 1, 1),
(1031, 56, 1, 'Laptop Asus Vivobook 15 OLED A1505ZA', 'laptop-asus-vivobook-15-oled-a1505za', 'Laptop Asus Vivobook 15 OLED A1505ZA i5 12500H (L1337W) có không gian hiển thị rộng rãi, sắc nét với màn hình 15.6 inch OLED cùng nhiều hiệu năng mạnh mẽ khác. Đây chắc hẳn là chiếc laptop đồ họa - kỹ thuật phù hợp với những bạn đang có đòi hỏi về đồ họa hay các công việc sáng tạo.', 'Laptop Asus Vivobook trang bị bộ xử lý Intel Core i5 - 12500H mạnh mẽ giúp bạn xử lý trơn tru.', '20240624031925.jpg', 150000000, 100000000, '2024-06-23 20:19:25', '2024-06-25 20:58:59', 1, 1, 1),
(1032, 56, 1, 'Laptop Asus Vivobook 15 OLED A1505VA', 'laptop-asus-vivobook-15-oled-a1505va', 'Bạn đang tìm kiếm cho mình một mẫu laptop học tập - văn phòng mang hiệu năng xử lý mạnh mẽ, khung hình hiển thị sắc nét cùng đa dạng các tính năng hiện đại. Laptop Asus Vivobook 15 OLED A1505VA i5 13500H (L1341W) là một trong những lựa chọn hàng đầu cho việc đáp ứng hoàn hảo nhu cầu công việc, học tập cũng như giải trí thường ngày.', 'Cấu hình vượt trội, đáp ứng đa dạng nhu cầu.', '20240624032041.jpg', 23030000, 19920000, '2024-06-23 20:20:41', '2024-06-25 20:58:58', 1, 1, 1),
(1033, 56, 1, 'Laptop Asus Zenbook 14 OLED UX3405MA', 'laptop-asus-zenbook-14-oled-ux3405ma', 'Mở đầu cho kỷ nguyên laptop mới, hiện đại, thông minh, laptop Asus Zenbook 14 OLED UX3405MA Ultra 5 (PP151W) sở hữu con chip Intel Meteor Lake hoàn toàn mới, được tích hợp hàng loạt những tính năng AI hữu ích, màn hình chuẩn sắc nét. Mẫu sản phẩm này chắc chắn sẽ nâng tầm đáng kể cho phong cách làm việc của bạn.', 'Làm việc thông minh, tiện dụng hơn.', '20240624032332.jpg', 31970000, 29270000, '2024-06-23 20:23:32', '2024-06-25 20:59:29', 1, 1, 1),
(1034, 56, 1, 'Laptop Asus TUF Gaming F15 FX507VU i7', 'laptop-asus-tuf-gaming-f15-fx507vu-i7', 'Ngoại hình hầm hố, mạnh mẽ cùng cấu hình vượt trội đến từ con chip Intel dòng H hiệu năng cao, card RTX 40 series,... cũng như được tích hợp với các công nghệ hiện đại khác. Laptop Asus TUF Gaming F15 FX507VU i7 13620H (LP167W) hứa hẹn sẽ cho các anh em gamer được trải nghiệm những đấu trường ảo thật hấp dẫn.', 'Tự tin phô diễn mọi kĩ năng.', '20240624032506.jpg', 34970000, 30870000, '2024-06-23 20:25:06', '2024-06-25 20:58:54', 1, 1, 1),
(1035, 56, 1, 'Laptop Asus Zenbook 14 OLED UX3405MA Ultra 7', 'laptop-asus-zenbook-14-oled-ux3405ma-ultra-7', 'Laptop Asus Zenbook 14 OLED UX3405MA Ultra 7 155H (PP152W) một trong những mẫu máy xách tay tiên phong cho phong cách làm việc tương lai với việc sở hữu con chip Intel AI thế hệ mới với nhiều tính năng thông minh được tích hợp, màn hình OLED rực rỡ,... Đây chính xác là mẫu sản phẩm nên được lựa chọn trong năm nay.', 'Kiểu mẫu của sự sang trọng, tối giản.', '20240624032724.jpg', 34990000, 32890000, '2024-06-23 20:27:24', '2024-06-25 20:58:50', 1, 1, 1),
(1036, 56, 1, 'Laptop Asus Vivobook 15 X1504VA i7', 'laptop-asus-vivobook-15-x1504va-i7', 'Laptop Asus Vivobook 15 X1504VA i7 1355U (NJ023W) được trang bị bộ vi xử lý thế hệ thứ 13 mới nhất của Intel, mang lại hiệu suất cao có thể xử lý đa tác vụ mượt mà hay vận hành trơn tru các tác vụ như chỉnh sửa hình ảnh, lập trình và chơi game cơ bản.', 'Laptop Asus Vivobook sở hữu bộ vi xử lý Intel Core i7 1355U thế hệ thứ 13 mạnh mẽ.', '20240624032829.jpg', 20920000, NULL, '2024-06-23 20:28:29', '2024-06-25 20:58:48', 1, 1, 1),
(1038, 56, 1, 'Laptop Asus Zenbook 14 OLED UX3405MA Ultra 7', 'laptop-asus-zenbook-14-oled-ux3405ma-ultra-7', 'Laptop Asus Zenbook 14 OLED UX3405MA Ultra 7 155H (PP152W) một trong những mẫu máy xách tay tiên phong cho phong cách làm việc tương lai với việc sở hữu con chip Intel AI thế hệ mới với nhiều tính năng thông minh được tích hợp, màn hình OLED rực rỡ,... Đây chính xác là mẫu sản phẩm nên được lựa chọn trong năm nay.', NULL, '20240624033317.jpg', 37970000, 35270000, '2024-06-23 20:33:17', '2024-06-25 20:58:46', 1, 1, 1),
(1039, 56, 1, 'Laptop Asus Vivobook 15 X1504VA i5', 'laptop-asus-vivobook-15-x1504va-i5', 'Laptop Asus Vivobook 15 X1504VA i5 (NJ025W) được trang bị những thông số cấu hình mạnh mẽ và thiết kế đẹp mắt, một sản phẩm lý tưởng cho người dùng cần một chiếc laptop học tập - văn phòng đáp ứng nhu cầu làm việc và giải trí hàng ngày.', 'Thiết kế tinh giản, diện mạo hiện đại.', '20240624033415.jpg', 21030000, 16720000, '2024-06-23 20:34:15', '2024-06-25 20:58:45', 1, 1, 1),
(1040, 64, 19, 'Điện thoại OPPO A58 8GB/128GB', 'dien-thoai-oppo-a58-8gb128gb', 'Thị trường điện thoại di động ngày nay, OPPO A58 8GB là một trong những sản phẩm nổi bật với thiết kế vuông vức và hiện đại. Được thiết kế với mục tiêu tối ưu hóa trải nghiệm người dùng, chiếc điện thoại này mang đến một loạt tính năng ấn tượng trong một thiết kế thon gọn và nhẹ nhàng.', 'Thiết kế trẻ trung, sang trọng.', '20240624033858.jpg', 6280000, 6120000, '2024-06-23 20:38:58', '2024-06-25 20:58:44', 1, 1, 1),
(1041, 64, 18, 'Điện thoại iPhone 15 Pro 128GB', 'dien-thoai-iphone-15-pro-128gb', 'realme C65 (8GB/256GB) là một trong những chiếc điện thoại thông minh độc đáo và đáng chú ý trong phân khúc giá phổ thông. Với thiết kế hiện đại và các tính năng thú vị, hứa hẹn mang lại trải nghiệm tuyệt vời cho người dùng.', 'Vẻ ngoài đẹp mắt thu hút ánh nhìn', '20240624034204.jpg', 25490000, NULL, '2024-06-23 20:42:04', '2024-06-25 20:58:42', 1, 1, 1),
(1042, 64, 17, 'Điện thoại Samsung Galaxy A55 5G 12GB/256GB', 'dien-thoai-samsung-galaxy-a55-5g-12gb256gb', 'Samsung Galaxy A55 5G, mẫu điện thoại mới của dòng Galaxy A, ra mắt với nhiều công nghệ tiên phong kèm theo kết nối 5G nhanh chóng. Được giới thiệu như một lựa chọn đa năng, chất lượng cao nhưng có mức giá hợp lý, hứa hẹn sẽ là sản phẩm đáng chú ý trên thị trường.', 'Camera 50 MP cho khả năng chụp ảnh sắc nét.', '20240624034327.jpg', 14470000, 13320000, '2024-06-23 20:43:27', '2024-06-25 20:58:44', 1, 1, 1),
(1043, 64, 19, 'Điện thoại OPPO A18 4GB/64GB', 'dien-thoai-oppo-a18-4gb64gb', 'OPPO A18 - một trong những sản phẩm điện thoại giá rẻ được OPPO giới thiệu tại thị trường Việt Nam trong những tháng cuối năm 2023. Thiết kế của máy vẫn giữ nguyên phong cách quen thuộc như các sản phẩm điện thoại OPPO A, đi kèm với đó là một màn hình sắc nét cùng một hiệu năng ổn định.', 'Thiết kế trẻ trung bật tung cá tính.', '20240624034448.jpg', 4080000, 3920000, '2024-09-11 18:56:03', '2024-09-22 04:11:57', 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_topic`
--

CREATE TABLE `cdtt_topic` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(1000) NOT NULL,
  `slug` varchar(1000) NOT NULL,
  `sort_order` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `description` varchar(1000) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int(10) UNSIGNED NOT NULL,
  `updated_by` int(10) UNSIGNED DEFAULT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_topic`
--

INSERT INTO `cdtt_topic` (`id`, `name`, `slug`, `sort_order`, `description`, `created_at`, `updated_at`, `created_by`, `updated_by`, `status`) VALUES
(3, 'Tin tức', 'tin-tuc', 0, 'Mô tả tin tức', '2023-10-16 05:30:30', '2023-10-16 05:33:26', 1, NULL, 1),
(4, 'Dịch vụ', 'dich-vu', 0, 'Chủ đề dịch vụ', '2023-10-16 05:30:48', '2024-06-16 02:47:42', 1, 1, 1),
(8, 'công nghệ', 'cong-nghe', 0, NULL, '2024-06-25 09:03:20', '2024-10-06 20:24:33', 1, 1, 1),
(9, 'blog', 'blog', 0, NULL, '2024-06-25 09:03:11', '2024-06-25 09:03:11', 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `cdtt_user`
--

CREATE TABLE `cdtt_user` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `gender` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `roles` enum('admin','customer') NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `remember_token` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int(10) UNSIGNED NOT NULL,
  `updated_by` int(10) UNSIGNED DEFAULT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cdtt_user`
--

INSERT INTO `cdtt_user` (`id`, `name`, `username`, `password`, `gender`, `phone`, `email`, `roles`, `image`, `address`, `remember_token`, `created_at`, `updated_at`, `created_by`, `updated_by`, `status`) VALUES
(10, 'Bui Huu Danh', 'huudanh', '$2y$12$nccxKROIaZR8Y79OIB1.q.gRWlBGCR5lv9/zg18M86SZhxmJOaUGC', '1', '0755425639', 'huudanh@gmail.com', 'customer', '20241029021054.png', 'Thu Duc', NULL, '2024-10-28 18:13:13', '2024-11-01 09:09:23', 1, 1, 1),
(15, 'cao son lam', 'sonlam', '$2y$12$RdKw/TWARGGJNyoGS0dxGubVI/CyKapSYR3H3WL1pBuKF5LPmifAO', '1', '0254784123', 'lam@gmail.com', 'customer', NULL, 'Ho Chi Minh', NULL, '2024-11-01 10:39:46', '2024-11-01 10:42:26', 1, 1, 1),
(16, 'Nguyen Thanh Tu', 'thanhtu', '$2y$12$FJW4RXOC/gfeTaTJ9ihM1uE4idRTaPrmeHnA/OuQwZuS5/nE.jlj6', '1', '0274158965', 'thanhtu@gmail.com', 'customer', NULL, 'Thu Duc', NULL, '2024-11-01 10:41:33', '2024-11-01 10:41:33', 1, NULL, 1),
(20, 'Ho Nhat Khiem', 'nhatkhiem', '$2y$12$IVDMHMUtpIKcGuOdIQciP.Ki7fjLSzTFMsiEbWHtIWxzQYTu8KOvi', '1', '0753823639', 'khiem@gmail.com', 'customer', NULL, 'Thu Duc', NULL, '2024-11-01 10:50:57', '2024-11-01 10:50:57', 1, NULL, 1),
(22, 'Hoang Van The', 'vanthe', '$2y$12$/2DSroRVbwQAy6EYZnpjI.xHTGCxkf6CDBBe2aqx34QtVgN77dPA.', '1', '0123456789', 'vanthe@gmail.com', 'customer', NULL, 'Thu Duc', NULL, '2024-11-04 17:54:19', '2024-11-04 17:54:19', 1, NULL, 1),
(23, 'Huynh Tan Phat', 'tanphat', '$2y$12$eeqi2Hc7gnLXPbdXxoE8C.OOhoKRXV6A5Rz.CldVuFGlTs1kw3kee', '1', '0123456789', 'phat@gmail.com', 'customer', NULL, 'Thu Duc', NULL, '2024-11-04 20:28:43', '2024-11-04 20:28:43', 1, NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `tokenable_type` varchar(191) NOT NULL,
  `tokenable_id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(191) NOT NULL,
  `token` varchar(64) NOT NULL,
  `abilities` text DEFAULT NULL,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `personal_access_tokens`
--

INSERT INTO `personal_access_tokens` (`id`, `tokenable_type`, `tokenable_id`, `name`, `token`, `abilities`, `last_used_at`, `expires_at`, `created_at`, `updated_at`) VALUES
(1, 'App\\Models\\User', 10, 'authToken', '3b296dc8a5c89cabf7421d46147d079e6e3a8cebaff6de3cc6be4cd592ee0f6d', '[\"*\"]', NULL, NULL, '2024-10-28 18:45:03', '2024-10-28 18:45:03'),
(2, 'App\\Models\\User', 10, 'authToken', 'edd3fe8f065079dbfafaa8b3082dd348dcb893c2633fefa3636bb7eb294276bb', '[\"*\"]', NULL, NULL, '2024-10-28 18:45:24', '2024-10-28 18:45:24'),
(3, 'App\\Models\\User', 10, 'authToken', '85e402894c8fa6c8c019c89a63c3a76f027abe412bacaa734f552a1424cfdee2', '[\"*\"]', NULL, NULL, '2024-10-28 18:58:47', '2024-10-28 18:58:47'),
(4, 'App\\Models\\User', 10, 'authToken', 'f90eb0b3bc1ea8b4e86da75f890c9d6793abea39f2a5a589c0c9de490231bef2', '[\"*\"]', NULL, NULL, '2024-10-28 18:59:46', '2024-10-28 18:59:46'),
(5, 'App\\Models\\User', 10, 'authToken', '6eeeac058676a9799890b39ebd7f3f2ac80b8b493edc9d606f67a420fbecc0d5', '[\"*\"]', NULL, NULL, '2024-10-28 19:07:47', '2024-10-28 19:07:47'),
(6, 'App\\Models\\User', 10, 'authToken', 'ab9cef09c7a8d3e2e067d957aee56e2e9e294186a9509a04edba00bce5257ca5', '[\"*\"]', NULL, NULL, '2024-10-28 20:01:17', '2024-10-28 20:01:17'),
(7, 'App\\Models\\User', 10, 'authToken', 'a1530b1957090c1b82fa4bec74a163fbbaffbd887d0e7b089137d3fa74fdade7', '[\"*\"]', NULL, NULL, '2024-10-28 20:02:12', '2024-10-28 20:02:12'),
(8, 'App\\Models\\User', 10, 'authToken', 'd322b74303b3b2077200f53cecb79ee8939d895acacae7fe083c1c7b5fdb333d', '[\"*\"]', NULL, NULL, '2024-10-28 20:14:56', '2024-10-28 20:14:56'),
(9, 'App\\Models\\User', 10, 'authToken', '2295cd1b7a9b3e8c63caf64901535a43aa9076bf036bb1549b6149a3d22e9eef', '[\"*\"]', NULL, NULL, '2024-10-28 20:14:59', '2024-10-28 20:14:59'),
(10, 'App\\Models\\User', 10, 'authToken', 'cf52b4fcb88c389ced21f9e02aea604773ba1d97435e9faa61b925735da16468', '[\"*\"]', NULL, NULL, '2024-10-28 20:15:11', '2024-10-28 20:15:11'),
(11, 'App\\Models\\User', 12, 'authToken', '5ffe58e7fa76bfb8c55f6850f26b48cd1752d4cf39d01e51cf347f6611f021d2', '[\"*\"]', NULL, NULL, '2024-10-28 21:07:38', '2024-10-28 21:07:38'),
(12, 'App\\Models\\User', 10, 'authToken', '78901357b10a67306aab43896e49830442e82d00801e0d8a2b6f2d915e6be4e3', '[\"*\"]', NULL, NULL, '2024-10-28 21:23:58', '2024-10-28 21:23:58'),
(13, 'App\\Models\\User', 13, 'authToken', 'fa58ac2f8e2543124642ed5b013f2d231317c2dc6853f19c4ce1836e1427008a', '[\"*\"]', NULL, NULL, '2024-10-28 21:28:59', '2024-10-28 21:28:59'),
(15, 'App\\Models\\User', 10, 'authToken', '765ec51d083f39ec8d083a4cc1fb1f2fb97beb0199715205e50944ecb4f51169', '[\"*\"]', NULL, NULL, '2024-11-01 08:32:36', '2024-11-01 08:32:36'),
(21, 'App\\Models\\User', 10, 'authToken', 'bb3bb4255549e481cb5e11b8b6567db60374d821b7fe888df241a7252ff2f64f', '[\"*\"]', NULL, NULL, '2024-11-01 09:34:09', '2024-11-01 09:34:09'),
(32, 'App\\Models\\User', 10, 'authToken', '96d98d15856a4ae29eab1847f18dd73cd5575659c89adda8d2ed8a990eac3712', '[\"*\"]', NULL, NULL, '2024-11-04 17:47:45', '2024-11-04 17:47:45'),
(33, 'App\\Models\\User', 10, 'authToken', 'c5369eaba50f1d1e1510f4c06a7b66d0baa3be33eb3537e494ebf498388669bb', '[\"*\"]', NULL, NULL, '2024-11-04 17:55:01', '2024-11-04 17:55:01'),
(34, 'App\\Models\\User', 10, 'authToken', '1b4f338b437bc0cf0cd97e1134ad8a53f83a9847cd4a4b5f7eb1fbe8262a63fd', '[\"*\"]', NULL, NULL, '2024-11-04 18:09:31', '2024-11-04 18:09:31'),
(35, 'App\\Models\\User', 10, 'authToken', '31a67e1f8d17b50e71aa0f339a615bb2cca4ca4db081832bef5430e56affc032', '[\"*\"]', NULL, NULL, '2024-11-04 18:46:39', '2024-11-04 18:46:39'),
(36, 'App\\Models\\User', 10, 'authToken', 'eb12d530e8fa0222f09bb59470b7b829e63919a314389529740357f78dbf2955', '[\"*\"]', NULL, NULL, '2024-11-04 18:59:10', '2024-11-04 18:59:10'),
(37, 'App\\Models\\User', 10, 'authToken', 'cbbd14ecc827bb3baa4e665bcff8f11b4d08f59519cea06e8a0757817d11421b', '[\"*\"]', NULL, NULL, '2024-11-04 19:16:04', '2024-11-04 19:16:04'),
(38, 'App\\Models\\User', 10, 'authToken', 'dc2d47bb5c2378ea5bb23e52ce6626b7d29b0c1759387dde21125d39512c6dc8', '[\"*\"]', NULL, NULL, '2024-11-04 19:21:17', '2024-11-04 19:21:17'),
(39, 'App\\Models\\User', 10, 'authToken', '940414556755e3c0a112cc3793f9e8fbcfad3d1d1fcdcc42f23aed1d7122e375', '[\"*\"]', NULL, NULL, '2024-11-04 19:30:02', '2024-11-04 19:30:02'),
(40, 'App\\Models\\User', 10, 'authToken', '85b291b436d080f2c61ae7eddbf05760d35a14d9b7e0a408c466468e6ca62d61', '[\"*\"]', NULL, NULL, '2024-11-04 19:37:46', '2024-11-04 19:37:46'),
(41, 'App\\Models\\User', 10, 'authToken', '59202bacbdc494a068ec93d1ecc118f72915a120b5669cbc3f7618b0a28966ee', '[\"*\"]', NULL, NULL, '2024-11-04 19:49:24', '2024-11-04 19:49:24'),
(42, 'App\\Models\\User', 10, 'authToken', 'de34b96b5d378160ceef71219fd7c75f1b053f39a0e7b4c822c85e6aefbf9dd5', '[\"*\"]', NULL, NULL, '2024-11-04 20:21:27', '2024-11-04 20:21:27'),
(43, 'App\\Models\\User', 23, 'authToken', '41bbc4afe2a8e30964eae354faf269949eabd6281290fb9932081ff12c15ce3c', '[\"*\"]', NULL, NULL, '2024-11-04 20:29:00', '2024-11-04 20:29:00'),
(47, 'App\\Models\\User', 10, 'authToken', '6b112dafda8251e84ec8e49673ad355bd56ed05bcadde7449d7fdd40e11a8550', '[\"*\"]', '2024-11-05 09:55:21', NULL, '2024-11-05 08:53:14', '2024-11-05 09:55:21'),
(48, 'App\\Models\\User', 10, 'authToken', '98db8046a2421df9f760e21fe4ce8fd81b4dad0d63722c0b7773beaf8c1802bf', '[\"*\"]', '2024-11-05 09:09:04', NULL, '2024-11-05 09:05:44', '2024-11-05 09:09:04');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cdtt_banner`
--
ALTER TABLE `cdtt_banner`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_brand`
--
ALTER TABLE `cdtt_brand`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_category`
--
ALTER TABLE `cdtt_category`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_contact`
--
ALTER TABLE `cdtt_contact`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_menu`
--
ALTER TABLE `cdtt_menu`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_order`
--
ALTER TABLE `cdtt_order`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_orderdetail`
--
ALTER TABLE `cdtt_orderdetail`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_post`
--
ALTER TABLE `cdtt_post`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_product`
--
ALTER TABLE `cdtt_product`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_topic`
--
ALTER TABLE `cdtt_topic`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cdtt_user`
--
ALTER TABLE `cdtt_user`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `personal_access_tokens_token_unique` (`token`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cdtt_banner`
--
ALTER TABLE `cdtt_banner`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `cdtt_brand`
--
ALTER TABLE `cdtt_brand`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `cdtt_category`
--
ALTER TABLE `cdtt_category`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=70;

--
-- AUTO_INCREMENT for table `cdtt_contact`
--
ALTER TABLE `cdtt_contact`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `cdtt_menu`
--
ALTER TABLE `cdtt_menu`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=130;

--
-- AUTO_INCREMENT for table `cdtt_order`
--
ALTER TABLE `cdtt_order`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `cdtt_orderdetail`
--
ALTER TABLE `cdtt_orderdetail`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=74;

--
-- AUTO_INCREMENT for table `cdtt_post`
--
ALTER TABLE `cdtt_post`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `cdtt_product`
--
ALTER TABLE `cdtt_product`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1051;

--
-- AUTO_INCREMENT for table `cdtt_topic`
--
ALTER TABLE `cdtt_topic`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `cdtt_user`
--
ALTER TABLE `cdtt_user`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
