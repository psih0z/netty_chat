CREATE TABLE `darcity_db`.`messages` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `time` INT NULL,
  `name` VARCHAR(64) NULL,
  `msg` VARCHAR(512) NULL,
  `room` VARCHAR(64) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;