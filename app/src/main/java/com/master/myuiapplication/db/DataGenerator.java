/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.master.myuiapplication.db;

import android.util.Log;

import com.master.myuiapplication.db.entity.BrushDataEntity;
//import com.master.myuiapplication.db.entity.CommentEntity;
//import com.master.myuiapplication.db.entity.ProductEntity;
//import com.master.myuiapplication.model.Product;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Generates data to pre-populate the database
 */
public class DataGenerator {

    private static final int DBSIZE = 50;
    private static final String[] BRUSHNAME = new String[]{
            "DMBrush 1", "DMBrush ABCDEF", "DMBrush 123456", "DMBrush 7890"};
    private static final LocalDateTime[] dateTimeArray = new LocalDateTime[]{
            LocalDateTime.now(), LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(5),
            LocalDateTime.now().minusDays(6), LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(8),
            LocalDateTime.now().minusDays(9), LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(11),
            LocalDateTime.now().minusWeeks(2), LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(12),
            LocalDateTime.now().minusHours(23), LocalDateTime.now().minusHours(24), LocalDateTime.now().minusHours(25),
            LocalDateTime.now().minusHours(34), LocalDateTime.now().minusHours(50), LocalDateTime.now().minusHours(1),
            LocalDateTime.now().minusMinutes(1439), LocalDateTime.now().minusSeconds(86399)
    };

    public static List<BrushDataEntity> generateValues() {
        List<BrushDataEntity> brushdatavalues = new ArrayList<>(DBSIZE);
        byte[] dummyData = new byte[180];
        int dummyData1;
//        for(int i=0; i<256; i++) dummyData[i] = (byte)i;
//        for(int i=0; i<180; i++) dummyData[i] = (byte)new Random().nextInt(60 + 1);

        for (int i = 0; i < DBSIZE; i++) {
            BrushDataEntity brushdata = new BrushDataEntity();
            brushdata.setBrushName(BRUSHNAME[new Random().nextInt(BRUSHNAME.length)]);
//            brushdata.setLocalDateTime(LocalDateTime.now());
            brushdata.setLocalDateTime(dateTimeArray[new Random().nextInt(dateTimeArray.length)]);
// 180 bytes long dummy data [] contains random values between 0 ~ 60 min values
            // a byte can be anything from all 0's to all 1's
/*
            for(int k=0; k<180; k++) dummyData[k] = (byte)new Random().nextInt(255 + 1);
            brushdata.setBrushValue(dummyData);   // must change to int after the change
*/
            dummyData1 = (byte)new Random().nextInt(255 + 1);
            brushdata.setBrushValue(dummyData1);
            brushdatavalues.add(brushdata);
        }

        return brushdatavalues;
    }


    public static BrushDataEntity generateValue() {
        BrushDataEntity brushdatavalue = new BrushDataEntity();
        byte[] dummyData = new byte[180];
        int randNumBrushName = new Random().nextInt(BRUSHNAME.length);
        int randNumDateTime = new Random().nextInt(dateTimeArray.length);
        int dummyData1;

        brushdatavalue.setBrushName(BRUSHNAME[randNumBrushName]);
//        brushdatavalue.setLocalDateTime(LocalDateTime.now());
        brushdatavalue.setLocalDateTime(dateTimeArray[randNumDateTime]);
// 180 bytes long dummy data [] contains random values between 0 ~ 60 min values
// a byte can be anything from all 0's to all 1's
//        for(int i=0; i<256; i++) dummyData[i] = (byte)i;
/*
        for(int k=0; k<180; k++) dummyData[k] = (byte)new Random().nextInt(255 + 1);
        brushdatavalue.setBrushValue(dummyData);  // must change to int after the change
*/
        dummyData1 = (byte)new Random().nextInt(255 + 1);
        brushdatavalue.setBrushValue(dummyData1);

        Log.d("DataGenerator", " BrushDataEntity brushvalue " +randNumBrushName +" " +BRUSHNAME[randNumBrushName]
        +" " +randNumDateTime +" " +dateTimeArray[randNumDateTime] +" " +dummyData1);

        return brushdatavalue;
    }


/*

    private static final String[] FIRST = new String[]{
            "Special edition", "New", "Cheap", "Quality", "Used"};
    private static final String[] SECOND = new String[]{
            "Three-headed Monkey", "Rubber Chicken", "Pint of Grog", "Monocle"};
    private static final String[] DESCRIPTION = new String[]{
            "is finally here", "is recommended by Stan S. Stanman",
            "is the best sold product on Mêlée Island", "is \uD83D\uDCAF", "is ❤️", "is fine"};
    private static final String[] COMMENTS = new String[]{
            "Comment 1", "Comment 2", "Comment 3", "Comment 4", "Comment 5", "Comment 6"};

    public static List<ProductEntity> generateProducts() {
        List<ProductEntity> products = new ArrayList<>(FIRST.length * SECOND.length);
        Random rnd = new Random();
        for (int i = 0; i < FIRST.length; i++) {
            for (int j = 0; j < SECOND.length; j++) {
                ProductEntity product = new ProductEntity();
                product.setName(FIRST[i] + " " + SECOND[j]);
                product.setDescription(product.getName() + " " + DESCRIPTION[j]);
                product.setPrice(rnd.nextInt(240));
                product.setId(FIRST.length * i + j + 1);
                products.add(product);
            }
        }

        return products;
    }

    public static List<CommentEntity> generateCommentsForProducts(
            final List<ProductEntity> products) {
        List<CommentEntity> comments = new ArrayList<>();
        Random rnd = new Random();

        for (Product product : products) {
            int commentsNumber = rnd.nextInt(5) + 1;
            for (int i = 0; i < commentsNumber; i++) {
                CommentEntity comment = new CommentEntity();
                comment.setProductId(product.getId());
                comment.setText(COMMENTS[i] + " for " + product.getName());
                comment.setPostedAt(new Date(System.currentTimeMillis()
                        - TimeUnit.DAYS.toMillis(commentsNumber - i) + TimeUnit.HOURS.toMillis(i)));
                comments.add(comment);
            }
        }

        return comments;
    }
*/

}
