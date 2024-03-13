package dev.hana.hanatestapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.BranchContentSchema;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.CurrencyType;
import io.branch.referral.util.ProductCategory;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("myprod/1234")
                .setCanonicalUrl("https://test_canonical_url")
                .setTitle("test_title")
                .setContentMetadata(
                        new ContentMetadata()
                                .addCustomMetadata("custom_metadata_key1", "custom_metadata_val1")
                                .addCustomMetadata("custom_metadata_key1", "custom_metadata_val1")
                                .addCustomMetadata("product_id", "custom_metadata_val1")
                                .addImageCaptions("image_caption_1", "image_caption2", "image_caption3")
                                .setAddress("Street_Name", "test city", "test_state", "test_country", "test_postal_code")
                                .setRating(5.2, 6.0, 5)
                                .setLocation(-151.67, -124.0)
                                .setPrice(10.0, CurrencyType.USD)
                                .setProductBrand("test_prod_brand")
                                .setProductCategory(ProductCategory.APPAREL_AND_ACCESSORIES)
                                .setProductName("test_prod_name")
                                .setProductCondition(ContentMetadata.CONDITION.EXCELLENT)
                                .setProductVariant("test_prod_variant")
                                .setQuantity(1.5)
                                .setSku("test_sku")
                                .setContentSchema(BranchContentSchema.COMMERCE_PRODUCT)
                )
                .addKeyWord("keyword1")
                .addKeyWord("keyword2");

        new BranchEvent("PURCHASE")
                .setCustomerEventAlias("ANDROID APP PURCHASE")
                .setAffiliation("test_affiliation")
                .setCoupon("Coupon Code")
                .setCurrency(CurrencyType.USD)
                .setTransactionID("abcd-12345-zyx-test-001")
                .setDescription("Customer added item to cart")
                .setRevenue(10)
                .setSearchQuery("Test Search query")
                .addCustomDataProperty("Custom_Event_Property_Key2", "Custom_Event_Property_val2")
                .addContentItems(buo)
                .logEvent(this);

    }

}
