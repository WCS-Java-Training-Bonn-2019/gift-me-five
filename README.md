# gift-me-five
Creating and sharing wishlists

## Create database

```
CREATE DATABASE IF NOT EXISTS `gift_me_five`;
CREATE USER IF NOT EXISTS 'giftMeFive'@'localhost' IDENTIFIED BY '!giftMeFive01';
GRANT ALL PRIVILEGES ON gift_me_five.* TO 'giftMeFive'@'localhost';
```

### Creating updated schema.sql / data.sql files

```
mysqldump -u root -p --no-data gift_me_five > src/main/resources/schema.sql
mysqldump -u root -p --no-create-info gift_me_five > src/main/resources/data.sql

sed -i '1iSET FOREIGN_KEY_CHECKS=0;' src/main/resources/schema.sql
echo "SET FOREIGN_KEY_CHECKS=1;" >> src/main/resources/schema.sql
sed -i '1iSET FOREIGN_KEY_CHECKS=0;' src/main/resources/data.sql
echo "SET FOREIGN_KEY_CHECKS=1;" >> src/main/resources/data.sql
```
