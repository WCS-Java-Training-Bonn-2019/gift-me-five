#!/bin/sh
# Workbench Table Data copy script
# Workbench Version: 6.3.8
# 
# Execute this to copy table data from a source RDBMS to MySQL.
# Edit the options below to customize it. You will need to provide passwords, at least.
# 
# Source DB: Mysql@192.168.2.99:3306 (MySQL)
# Target DB: Mysql@localhost:3306


# Source and target DB passwords
arg_source_password=!giftMeFive01
arg_target_password=!giftMeFive01

if [ -z "$arg_source_password" ] && [ -z "$arg_target_password" ] ; then
    echo WARNING: Both source and target RDBMSes passwords are empty. You should edit this file to set them.
fi
arg_worker_count=2
# Uncomment the following options according to your needs

# Whether target tables should be truncated before copy
# arg_truncate_target=--truncate-target
# Enable debugging output
# arg_debug_output=--log-level=debug3

/usr/bin/mysql -u giftMeFive --password='!giftMeFive01' gift_me_five < migration_script.sql

/usr/lib/mysql-workbench/wbcopytables --mysql-source="giftMeFive@192.168.2.99:3306" --target="giftMeFive@localhost:3306" --source-password="$arg_source_password" --target-password="$arg_target_password" --thread-count=$arg_worker_count $arg_truncate_target $arg_debug_output --table '`gift_me_five`' '`wish`' '`gift_me_five`' '`wish`' '`id`' '`id`' '`id`, `create_date`, `description`, `link`, `modify_date`, `price`, `title`, `giver_id`, `wishlist_id`, `picture`' --table '`gift_me_five`' '`role`' '`gift_me_five`' '`role`' '`id`' '`id`' '`id`, `create_date`, `modify_date`, `role`' --table '`gift_me_five`' '`wishlist`' '`gift_me_five`' '`wishlist`' '`id`' '`id`' '`id`, `create_date`, `modify_date`, `title`, `unique_url_giver`, `unique_url_receiver`, `receiver_id`, `theme_id`' --table '`gift_me_five`' '`user`' '`gift_me_five`' '`user`' '`id`' '`id`' '`id`, `create_date`, `email`, `failed_logins`, `firstname`, `last_login`, `lastname`, `modify_date`, `password`, `role`, `reason`' --table '`gift_me_five`' '`theme`' '`gift_me_five`' '`theme`' '`id`' '`id`' '`id`, `background_picture`, `modify_date`' --table '`gift_me_five`' '`wishlist_givers`' '`gift_me_five`' '`wishlist_givers`' '-' '-' '`wishlist_id`, `user_id`'

