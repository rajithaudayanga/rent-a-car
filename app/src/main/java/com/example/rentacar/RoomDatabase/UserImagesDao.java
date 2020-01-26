package com.example.rentacar.RoomDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserImagesDao {

    @Insert
    void saveImage(UserImages userImages);

    @Query("SELECT image_bitmap FROM userimages where user_id=:user_id")
    String hasImage(String user_id);


}