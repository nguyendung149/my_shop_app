package com.example.myappshop.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val MYSHOPAPP_PREFERENCES = "MyShopAppPrefs"
    const val LOGGED_IN_USERNAME = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_detail"

    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE:String ="Male"
    const val FEMALE:String ="Female"

    const val MOBILE:String = "mobile"
    const val GENDER:String  = "gender"
    const val BIRTHDAY:String = "birthDay"
    const val ADDRESS:String = "address"
    const val USER_AVATAR:String = "image"
    const val COMPLETE_PROFILE : String = "profileCompleted"

    const val USER_PROFILE_AVATAR:String = "User_Image"
    const val PRODUCT_IMAGE:String = "Product_image"

    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"

    const val USER_INFO = "user_info"

    const val PRODUCT = "product"

    const val MESSAGE = "Messages"
    const val RECENT_CHAT = "recentChat"

    const val USER_ID = "user_id"

    const val EXTRA_PRODUCT_ID = "extra_product_id"


    const val EXTRA_PRODUCT_DETAILS = "extra_product_details"

    const val EXTRA_PRODUCT_OWNER_ID = "extra_product_owner_id"

    const val DEFAULT_CARD_QUANTITY = "1"

    const val CART_ITEMS = "cart_items"

    const val PRODUCT_ID = "product_id"

    const val CART_ITEM_QUANTITY = "cart_quantity"


    const val HOME = "Home"
    const val OFFICE = "Office"
    const val OTHERS = "Other"

    const val ADDRESSES = "addresses"


    const val EXTRA_ADDRESS_DETAIL = "extra_address_detail"


    const val  EXTRA_SELECT_ADDRESS: String = "extra_select_address"

    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"


    const val ORDERS = "orders"

    const val STOCK_QUANTITY = "stock_quantity"


    const val EXTRA_ORDER_DETAILS = "extra_order_details"


    const val SOLD_PRODUCTS = "sold_products"


    const val EXTRA_SOLD_PRODUCT_DETAILS = "extra_sold_product_details"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(activity: Activity,uri:Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}