-- Adds owner contact channels to each listing (phone / WeChat / email-QQ).
-- Run once against linju_find_db. Existing rows get NULLs and simply show no
-- contact card until edited.
--
--   mysql -u root linju_find_db < db/migration_add_listing_contact.sql
--
USE linju_find_db;

ALTER TABLE listing
    ADD COLUMN contact_phone  VARCHAR(20)  NULL AFTER property_type,
    ADD COLUMN contact_wechat VARCHAR(50)  NULL AFTER contact_phone,
    ADD COLUMN contact_email  VARCHAR(100) NULL AFTER contact_wechat;
