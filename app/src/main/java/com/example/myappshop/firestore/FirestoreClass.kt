package com.example.myappshop.firestore

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myappshop.models.Address
import com.example.myappshop.models.CartProductItem
import com.example.myappshop.models.Order
import com.example.myappshop.models.Product
import com.example.myappshop.models.RatingProduct
import com.example.myappshop.models.SoldProduct
import com.example.myappshop.ui.activities.users.DangKy
import com.example.myappshop.ui.activities.users.DangNhap
import com.example.myappshop.ui.activities.users.UserProfileActivity
import com.example.myappshop.models.User
import com.example.myappshop.ui.activities.DashboardActivity
import com.example.myappshop.ui.activities.addresses.AddressListActivity
import com.example.myappshop.ui.activities.addresses.EditAddressActivity
import com.example.myappshop.ui.activities.products.AddProductActivity
import com.example.myappshop.ui.activities.products.CartProductListActivity
import com.example.myappshop.ui.activities.products.CheckOutActivity
import com.example.myappshop.ui.activities.products.ProductDetailActivity
import com.example.myappshop.ui.activities.products.SearchProductActivity
import com.example.myappshop.ui.activities.users.SettingsActivity
import com.example.myappshop.ui.fragments.DashboardFragment
import com.example.myappshop.ui.fragments.OrdersFragment
import com.example.myappshop.ui.fragments.ProductsFragment
import com.example.myappshop.ui.fragments.SoldProductFragment
import com.example.myappshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: DangKy, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisterSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user."
                )
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.MYSHOPAPP_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()


                when (activity) {
                    is DangNhap -> {
                        activity.userLoggedInSuccess(user)
                    }

                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }

                    is CartProductListActivity -> {
                        activity.ShippingFee(user)
                    }

                    is CheckOutActivity -> {
                        activity.ShippingFee(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is DangNhap -> {
                        activity.hideProgressDialog()
                    }

                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun getUserDetailShop(activity: Activity, userID: String) {
        mFireStore.collection(Constants.USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.MYSHOPAPP_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()


                when (activity) {
                    is ProductDetailActivity -> {
                        activity.userDetailSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is ProductDetailActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating thee user details",
                    e
                )

            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType: String) {
        val sRef = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(activity, imageFileUri)
        )
        sRef.putFile(imageFileUri!!)
            .addOnSuccessListener { taskSnapshot ->
                //The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }

                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }

                    Log.e("Downloadable Image URL", uri.toString())

                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    e.message,
                    e
                )

            }


    }

    fun uploadProductDetails(activity: AddProductActivity, productDetail: Product) {
        mFireStore.collection(Constants.PRODUCT).document()
            .set(productDetail, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details",
                    exception
                )

            }
    }

    fun getProductList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCT).whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Product List", document.documents.toString())
                val productlist = ArrayList<Product>()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.id = i.id

                    productlist.add(product)
                }
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductListFromFireStore(productlist)
                    }
                }
            }

    }

    fun getProductListSearch(activity: Activity) {
        mFireStore.collection(Constants.PRODUCT).get()
            .addOnSuccessListener { document ->
                Log.e("Product List", document.documents.toString())
                val productlist = ArrayList<Product>()
                val titileProductList = ArrayList<String>()
                for (item in document.documents) {
                    val product = item.toObject(Product::class.java)!!
                    product.id = item.id
                    productlist.add(product)
                    titileProductList.add(product.title)
                }
                when (activity) {
                    is SearchProductActivity -> {
                        activity.successProductListFromFireStoreSearchView(
                            productlist,
                            titileProductList
                        )
                    }
                }
            }

    }

    fun getDashboardItemList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCT)
            .whereNotEqualTo(Constants.USER_ID, getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())


                val productlist = ArrayList<Product>()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)!!
                    product.id = i.id

                    productlist.add(product)
                }
                fragment.successDashboardItemLists(productlist)

            }
            .addOnFailureListener { exception ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting dashboard items list! ",
                    exception
                )

            }
    }

    fun getProductDetails(activity: ProductDetailActivity, productID: String) {
        mFireStore.collection(Constants.PRODUCT).document(productID).get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val product: Product = document.toObject(Product::class.java)!!
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the product details",
                    exception
                )

            }
    }

    fun deleteProduct(fragment: ProductsFragment, productID: String) {
        mFireStore.collection(Constants.PRODUCT).document(productID)
            .delete()
            .addOnSuccessListener {
                fragment.successDeleteProduct()
            }
            .addOnFailureListener { exception ->
                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName, "Error while deleting the product!", exception)

            }
    }

    fun addCartItem(activity: ProductDetailActivity, addToCart: CartProductItem) {
        mFireStore.collection(Constants.CART_ITEMS).document().set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item",
                    exception
                )

            }
    }

    fun checkProductItemExistInCart(activity: ProductDetailActivity, productID: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .whereEqualTo(Constants.PRODUCT_ID, productID)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                if (document.documents.size > 0) {
                    activity.productExistInCartList()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list",
                    exception
                )

            }
    }

    fun getCartProductList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                var cartProductList: ArrayList<CartProductItem> = ArrayList()

                for (i in document) {
                    val cartItem: CartProductItem = i.toObject(CartProductItem::class.java)
                    cartItem.id = i.id
                    cartProductList.add(cartItem)
                }
                when (activity) {
                    is CartProductListActivity -> {
                        activity.getCartProductListSuccess(cartProductList)
                    }

                    is CheckOutActivity -> {
                        activity.successCartItemList(cartProductList)
                    }
                }

            }
            .addOnFailureListener { e ->

                when (activity) {
                    is CartProductListActivity -> {
                        activity.hideProgressDialog()

                    }

                    is CheckOutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the cart list items",
                    e
                )
            }
    }

    fun getAllProductList(activity: Activity) {
        mFireStore.collection(Constants.PRODUCT).get()
            .addOnSuccessListener { document ->
                Log.e(
                    "Product list",
                    document.documents.toString()
                )
                var productList: ArrayList<Product> = ArrayList()
                for (item in document.documents) {
                    var product: Product = item.toObject(Product::class.java)!!
                    product.id = item.id
                    productList.add(product)
                }
                when (activity) {
                    is CartProductListActivity -> {
                        activity.getAllProductListSuccess(productList)
                    }

                    is CheckOutActivity -> {
                        activity.successProductListFromFireStore(productList)
                    }
                }


            }
            .addOnFailureListener { error ->
                when (activity) {
                    is CartProductListActivity -> activity.hideProgressDialog()
                    is CheckOutActivity -> activity.hideProgressDialog()
                }


                Log.e(
                    "Get Product List",
                    "Error while getting all product list!",
                    error
                )
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFireStore.collection(Constants.CART_ITEMS).document(cart_id).delete()
            .addOnSuccessListener {
                when (context) {
                    is CartProductListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { error ->
                when (context) {
                    is CartProductListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    error
                )
            }
    }

    fun updateCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS).document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartProductListActivity -> {
                        context.upDateCartSuccess()
                    }
                }
            }
            .addOnFailureListener { exception ->
                when (context) {
                    is CartProductListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating cart item list!",
                    exception
                )
            }
    }

    fun addAddressOnFireStore(activity: EditAddressActivity, address: Address) {
        mFireStore.collection(Constants.ADDRESSES).document().set(address, SetOptions.merge())
            .addOnSuccessListener {
                activity.addAddressSuccess()
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding address!",
                    exception
                )

            }

    }

    fun getAddressFronFireStore(activity: AddressListActivity) {
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(
                    activity.javaClass.simpleName,
                    document.documents.toString()
                )
                val addresslist = ArrayList<Address>()
                for (item in document.documents) {
                    var address: Address = item.toObject(Address::class.java)!!
                    address.id = item.id

                    addresslist.add(address)
                }
                activity.getAddressListFromFireStoreSuccess(addresslist)
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting a address list!",
                    exception
                )
            }
    }

    fun updateAddressOnFireStore(
        activity: EditAddressActivity,
        addressInfo: Address,
        addressID: String
    ) {
        mFireStore.collection(Constants.ADDRESSES).document(addressID)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addAddressSuccess()
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()


                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    exception
                )
            }

    }

    fun deleteAddressOnFireStore(activity: AddressListActivity, addressID: String) {
        mFireStore.collection(Constants.ADDRESSES).document(addressID).delete()
            .addOnSuccessListener {
                activity.deleteAddressOnFireStoreSuccess()
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting addresses!",
                    exception
                )

            }
    }

    fun placeOrder(activity: CheckOutActivity, order: Order) {
        mFireStore.collection(Constants.ORDERS).document().set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlaceSuccess()

            }
            .addOnFailureListener { exception ->

                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order",
                    exception
                )

            }
    }

    fun updateAllDetails(
        activity: CheckOutActivity,
        cartList: ArrayList<CartProductItem>,
        order: Order
    ) {
        val writeBatch = mFireStore.batch()

        // Prepare the sold product details
        for (cart in cartList) {

            val soldProduct = SoldProduct(
                FirestoreClass().getCurrentUserId(),
                cart.title,
                cart.price,
                cart.cart_quantity,
                cart.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )

            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document()
            writeBatch.set(documentReference, soldProduct)
        }

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCT)
                .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(
                activity.javaClass.simpleName,
                "Error while updating all the details after order placed.",
                e
            )
        }
    }

    fun getMyOrderList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS).whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val orderList: ArrayList<Order> = ArrayList()
                for (item in document.documents) {
                    val orderItem: Order = item.toObject(Order::class.java)!!
                    orderItem.id = item.id

                    orderList.add(orderItem)
                }
                fragment.populateOrdersListIInUI(orderList)

            }
            .addOnFailureListener { exception ->
                fragment.hideProgressDialog()

                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the order list",
                    exception
                )
            }

    }

    fun getSoldProductsList(fragment: SoldProductFragment) {
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<SoldProduct> = ArrayList()
                for (item in document.documents) {
                    val soldProduct = item.toObject(SoldProduct::class.java)!!

                    soldProduct.id = item.id

                    list.add(soldProduct)
                }
                fragment.successSoldProductsList(list)
            }
            .addOnFailureListener { exception ->

                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of sold products.",
                    exception
                )
            }

    }

    fun getUserCurrent(userID: String): User? {
        var user: User? = null
        mFireStore.collection(Constants.USERS).document(userID).get()
            .addOnFailureListener { exception ->
                Log.e(
                    "Error",
                    "Error while getting the order list",
                    exception
                )

            }
            .addOnSuccessListener { document ->
                user = document.toObject(User::class.java)!!

            }
        return user
    }

    fun storeRatingOnFireStore(ratingProduct: RatingProduct, productID: String) {
        mFireStore.collection("Rating $productID").document(ratingProduct.user_id).set(
            ratingProduct
        ).addOnSuccessListener {

        }.addOnFailureListener { exception ->
            Log.e(
                "Error",
                "Error while storing rating score",
                exception
            )
        }
    }

    fun calAverRatingScore(productID: String, activity: ProductDetailActivity) {
        mFireStore.collection("Rating ${productID}").get().addOnSuccessListener { document ->
            var SumStar: Double = 0.0
            var AverageStart: Double = 0.0
            var count: Int = 0
            for (item in document.documents) {
                val ratingProduct = item.toObject(RatingProduct::class.java)!!
                SumStar += ratingProduct.ratingScore.toDouble()
                count++
            }
            AverageStart = SumStar / count
            activity.calcAverageStart(AverageStart)

        }
            .addOnFailureListener { exception ->
                Log.e(
                    "Error",
                    "Error while storing rating score",
                    exception
                )
            }
    }

}



