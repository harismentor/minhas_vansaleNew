package com.advanced.minhas.adapter;

import static com.advanced.minhas.config.AmountCalculator.getTaxPrice;
import static com.advanced.minhas.config.AmountCalculator.getTaxPricefl;
import static com.advanced.minhas.config.ConfigKey.REQ_EDIT_TYPE;
import static com.advanced.minhas.config.ConfigValue.PRODUCT_UNIT_CASE;
import static com.advanced.minhas.config.Generic.getAmount;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.minhas.R;
import com.advanced.minhas.listener.OnNotifyListener;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.CartItem;
import com.advanced.minhas.textwatcher.TextValidator;

import java.util.ArrayList;

public class RvCartEditAdapter extends RecyclerView.Adapter<RvCartEditAdapter.RvCartHolder> {
    private Context context;
    private String TAG = "RvCartAdapter";
    public String st_disc = "";
    private MyDatabase myDatabase;
    private ArrayList<CartItem> cartItems;
    private ArrayList<CartItem> deleted_cartItems = new ArrayList<>();
    double db_qnty_kgm=0;
    int quantity_flag=0;

    OnNotifyListener listener;

    public RvCartEditAdapter(ArrayList<CartItem> cartItem, String st_disc, OnNotifyListener listener) {
        Log.e("reached inside","ok"+cartItem.size());
        // Log.e("cartItems inside",""+cartItems.get(0).getProductTotal());
        this.cartItems = cartItem;
        this.listener=listener;
        //  this.st_disc=st_disc;
    }

    @Override
    public RvCartHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);

        this.context = parent.getContext();
        myDatabase = new MyDatabase(parent.getContext());
        return new RvCartHolder(view);
    }

    @Override
    public void onBindViewHolder(RvCartHolder holder, final int position) {

        final CartItem cartItem = cartItems.get(position);

        int s = position + 1;

        holder.tvCode.setText(cartItem.getProductCode());

        holder.tvSlNo.setText(String.valueOf(s));
        holder.tvProductName.setText(cartItem.getProductName());

        holder.tvQty.setText(String.valueOf((int) cartItem.getTypeQuantity()));
        // holder.textView_item_free_qty.setText(String.valueOf(cartItem.getFreeQty()));

        holder.tvType.setText(cartItem.getOrderTypeName());
        Log.e("getOrderTypeName",""+cartItem.getOrderTypeName());
        holder.textView_item_discount.setText(""+cartItem.getProductDiscount());
        Log.e("netPricehrct cart1111", ""+cartItem.getNetPrice());
        double netPrice = cartItem.getNetPrice();

        Log.e("netPricehrct cart", ""+netPrice);
        if(cartItem.getTax_type().equals("TAX_INCLUSIVE")) {
            netPrice = cartItem.getProductPrice();
        }
        else{
            netPrice = cartItem.getProductPrice()-cartItem.getProductDiscount();
        }

        //netPrice = cartItem.getProductPrice();
        Log.e("netPrice cart", ""+netPrice);

        if (cartItem.getOrderType().equals(PRODUCT_UNIT_CASE)) {
            netPrice = netPrice * cartItem.getPiecepercart();
        }


        holder.tvUnitPrice.setText(getAmount(netPrice));

        Log.e("Discount STATUS", ""+cartItem.isDiscountStatus());
        Log.e("prodtotal cart", ""+cartItem.getProductTotal());

        holder.tvTotal.setText(getAmount(netPrice * cartItem.getTypeQuantity()));
        cartItem.setProductTotal(netPrice * cartItem.getTypeQuantity());
        //holder.tvTotal.setText(""+cartItem.getProductTotal());

        //   Log.e("Get Amount", ""+getAmount(netPrice * cartItem.getTypeQuantity()));

        String str_withouttax = ""+getAmount(netPrice * cartItem.getTypeQuantity());
        str_withouttax = str_withouttax.replace(",","");

        //  cartItems.get(position).setWithouttaxTotal(Double.parseDouble(str_withouttax)); // getAmount(netPrice * cartItem.getTypeQuantity()
        cartItem.setWithouttaxTotal(Double.parseDouble(str_withouttax));

        cartItems.get(position).setWithouttaxTotal(cartItem.getProductTotal()); //
        cartItem.setWithouttaxTotal(cartItem.getProductTotal());
        cartItems.get(position).setNetPrice((cartItem.getNetPrice()));
        /********************** edited on 18/11/2019 **************/

                /*holder.tvTotal.setText(getAmount(netPrice * cartItem.getTypeQuantity()));
                cartItems.get(position).setNetPrice(netPrice);*/

        /**********************************************************/

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });

        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);
                v.setAnimation(anim);

                showEditDialog(cartItem,position);

            }
        });

        if (listener!=null)
            listener.onNotified();

    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    private void delete(int position) { //removes the row

        try {
            Log.e("delete id",""+cartItems.get(position).getProductId());
           // myDatabase.deleteproduct_fromtemp(cartItems.get(position).getProductId());

          //  myDatabase.updateStockwhiledeletecart(cartItems.get(position).getProductId(), (int)cartItems.get(position).getTypeQuantity(),cartItems.get(position));
            CartItem cd = new CartItem();
            cd.setProductId(cartItems.get(position).getProductId());
            cd.setTypeQuantity(cartItems.get(position).getTypeQuantity());
            cd.setPieceQuantity(cartItems.get(position).getPieceQuantity());
            Log.e("qnty type",""+cartItems.get(position).getTypeQuantity());
            Log.e("qnty pcs",""+cartItems.get(position).getPieceQuantity());
            deleted_cartItems.add(cd);
            myDatabase.deleteproduct_fromtempedit(cartItems.get(position).getProductId());
            myDatabase.updateStock(cd, REQ_EDIT_TYPE);
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());

            if (cartItems.size()                                                                                                                                                                                                                                                                                                                                                                                                                                          == position) {
                if (listener!=null)
                    listener.onNotified();
            }
        }catch (IndexOutOfBoundsException e){
            Log.v(TAG,"delete   Exception "+e.getMessage());
        }
    }

    private void addItem(CartItem item) {
        try {
            Log.e("cartitem add", "" + cartItems.get(0).getNetPrice());
            Log.e("prodprice add", "" + cartItems.get(0).getProductPrice());

        }catch (Exception e){

        }
        cartItems.add(item);


        notifyItemInserted(cartItems.size() - 1);
        notifyDataSetChanged();
        notifyItemMoved(0, cartItems.size() - 1);
    }

    public void updateItem(CartItem item, int pos) {
//        Log.e("pos innn",""+pos);
//
//        cartItems.set(pos,item);
//
//        Log.e("update", ""+cartItems.get(pos).getProductTotal());
//        notifyItemChanged(pos);
    }

    public void updateItems(CartItem item, int pos) {
        Log.e("pos innnss",""+pos);

        cartItems.set(pos,item);

        Log.e("update", ""+cartItems.get(pos).getProductTotal());
        notifyItemChanged(pos);
    }
    private void showEditDialog(final CartItem cart, final int position) {
        Log.e("position edit",""+position);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

//        LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dialog_edit_cart, null);
        dialogBuilder.setView(dialogView);

        final TextView tvProductName, tvNetPrice, tvTotalPrice;
        final EditText etQuantity;
        final ImageButton ibQuantityDown, ibQuantityUp;

        tvProductName = (TextView) dialogView.findViewById(R.id.textView_editCart_product);
        tvNetPrice = (TextView) dialogView.findViewById(R.id.textView_editCart_unitPrice);
        tvTotalPrice = (TextView) dialogView.findViewById(R.id.textView_editCart_total);

        etQuantity = (EditText) dialogView.findViewById(R.id.EditText_editCart_Qty);

        ibQuantityDown = (ImageButton) dialogView.findViewById(R.id.imageButton_editCart_QtyDown);

        ibQuantityUp = (ImageButton) dialogView.findViewById(R.id.imageButton_editCart_QtyUp);



        tvProductName.setText(cart.getProductName());
        // tvNetPrice.setText(getAmount(cart.getNetPrice()));

        Log.e("net Price Dia", ""+cart.getNetPrice());
        Log.e("Pieces Dia", ""+cart.getPiecepercart());
        Log.e("OrderType Dia", ""+cart.getOrderType());


        double netPrice = cart.getNetPrice();
        if (cart.getOrderType().equals(PRODUCT_UNIT_CASE))
            netPrice = netPrice * cart.getPiecepercart();


        tvNetPrice.setText(getAmount(netPrice));

        tvTotalPrice.setText(""+getAmount(cart.getProductTotal()));
        //   tvTotalPrice.setText(getAmount(cart.getNetPrice() * cart.getTypeQuantity()));

        etQuantity.setText(String.valueOf(cart.getTypeQuantity()));

        etQuantity.addTextChangedListener(new TextValidator(etQuantity) {
            @Override
            public void validate(TextView textView, String qtyString) {

                try {
                    if (!TextUtils.isEmpty(qtyString)) {

                        float quantity = TextUtils.isEmpty(qtyString) ? 0 : Float.valueOf(qtyString);

                        if (cart.getOrderType().equals(PRODUCT_UNIT_CASE))
                            quantity = quantity * cart.getPiecepercart();

//                    haris added on 15-09-2020
                        String text1 = etQuantity.getText().toString().trim();

                        int tot_qnty = (int) quantity;

                        quantity_flag = 0;

                        if (quantity != 0) {

                            Log.e("cart if", "" + quantity);
                            double netprice = cart.getNetPrice();
//                            if(cart.getTax_type().equals("TAX_INCLUSIVE")) {
//                                netprice = cart.getProductPriceNew()/cart.getPieceQuantity_nw();
//                            }
//                            else{
//
//                            }
                            if (cart.getTax_type().equals("TAX_INCLUSIVE")) {
                                netprice = cart.getProductPrice();
                            } else {
                                netprice = cart.getNetPrice();
                            }
                            Log.e("taxvalu hrct frst", "" + cartItems.get(position).getTaxValue());
                            tvTotalPrice.setText(getAmount(netprice * quantity - (cart.getProductDiscount() * quantity)));
//                            double db_withouttax = Double.parseDouble(getAmount(cart.getNetPrice() * quantity)) - cart.getProductDiscount();
                            double db_withouttax = Double.parseDouble(getAmount(netprice * quantity)) - (cart.getProductDiscount() * quantity);

                            String str_withouttax = "" + getAmount(db_withouttax);
                            str_withouttax = str_withouttax.replace(",", "");
                            if(cart.getNetprice_draft()>0){
                                cart.setNetPrice(cart.getNetprice_draft());
                            }

                            cartItems.get(position).setWithouttaxTotal(Double.parseDouble(str_withouttax));
                            cartItems.get(position).setProductTotal(Double.parseDouble(str_withouttax));
                            cartItems.get(position).setProductTotalValue((cart.getNetPrice() * quantity));
                            cartItems.get(position).setTaxValue(getTaxPricefl((cart.getNetPrice() * quantity), cartItems.get(position).getTax(), cartItems.get(position).getTax_type()));

                            cartItems.get(position).setNetPrice((cart.getNetPrice()));

                            cartItems.get(position).setTaxValue(Double.parseDouble(getAmount(cartItems.get(position).getTaxValue())));
                            cart.setTaxValue(getTaxPricefl((cart.getNetPrice() * quantity)- (cart.getProductDiscount()* quantity), cartItems.get(position).getTax(), cartItems.get(position).getTax_type()));
                            Log.e("taxvalu hr", "" + cart.getTaxValue());
                            Log.e("taxvalu hrct", "" + cartItems.get(position).getTaxValue());
                            float fl_cgst = (float) (cart.getTaxValue() / 2);
                            double tothalf = 0;
                            if (fl_cgst > 0) {
                                tothalf = Double.parseDouble(getAmount(cart.getTaxValue() / 2));
                            }
                            Log.e("tothalf", "" + tothalf);
                            cart.setSgst((float) tothalf);
                            cart.setCgst((float) tothalf);
                            cartItems.get(position).setCgst((float) tothalf);
                            cartItems.get(position).setSgst((float) tothalf);
                            cart.setProductTotal(Double.parseDouble(str_withouttax));
                            cart.setCon_factor(cartItems.get(position).getCon_factor());
                            cartItems.get(position).setPieceQuantity((float) (quantity*cart.getCon_factor()));
                        } else {
                            Log.e("cart else", "" + quantity);
                            tvTotalPrice.setText(getAmount(cart.getNetPrice() * 0));
                            cartItems.get(position).setWithouttaxTotal(Double.parseDouble(getAmount(cart.getNetPrice() * 0)));
                            cartItems.get(position).setProductTotal(0);
                            cartItems.get(position).setProductTotalValue(0);
                            cartItems.get(position).setTaxValue(0);
                            cartItems.get(position).setCgst(0);
                            cartItems.get(position).setSgst(0);

                        }
                        // }

                    } else {
                        Log.e("prodname else", "" + cart.getProductName());
                        tvTotalPrice.setText(getAmount(cart.getNetPrice() * 0));
                        cartItems.get(position).setWithouttaxTotal(Double.parseDouble(getAmount(cart.getNetPrice() * 0)));

                    }
                }catch (Exception e){

                }

            }
        });

        ibQuantityDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    String text = etQuantity.getText().toString().trim();
                    float previousSize = 0;

                    if (!text.isEmpty())
                        previousSize = Float.parseFloat(text);

                    /**decrement numbers up to 1*/
                    if (previousSize > 1) {
                        previousSize--;
                        etQuantity.setText(String.valueOf(previousSize));
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        ibQuantityUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    String text = etQuantity.getText().toString().trim();

                    float nextSize = 0;

                    if (!text.isEmpty())
                        nextSize = Float.parseFloat(text);

                    /**increment numbers */

                    nextSize++;
//                    haris added on 15-09-2020
                    if(quantity_flag==0) {
                        etQuantity.setText(String.valueOf(nextSize));
                    }

                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

                String str = etQuantity.getText().toString().trim();

                if (!str.isEmpty()) {
                    float quantity = TextUtils.isEmpty(str) ? 0 : Float.valueOf(str);

                    float check_quantity = TextUtils.isEmpty(str) ? 0 : Float.valueOf(str);
                    if (cart.getOrderType().equals(PRODUCT_UNIT_CASE)) {
                        check_quantity = quantity*cart.getPiecepercart();
                    }

                    Log.e("quantity entered", ""+check_quantity);
                    Log.e("stock qty", ""+cart.getStockQuantity());


                    cart.setTypeQuantity(quantity);

                    if (cart.getOrderType().equals(PRODUCT_UNIT_CASE))
                        quantity = quantity * cart.getPiecepercart();

                    //  cart.setPieceQuantity(quantity);
                    cart.setPieceQuantity((float) (quantity*cart.getCon_factor()));

                    Log.e("pcs qty", ""+cart.getPieceQuantity());
                    cart.setPieceQuantity_nw(quantity);
                    //  double price = cart.getSalePrice() * quantity;
                    double netprice = cart.getNetPrice();
//                        if(cart.getTax_type().equals("TAX_INCLUSIVE")) {
//                            netprice = cart.getProductTotal()/cart.getPieceQuantity_nw();
//                        }
                    if(cart.getTax_type().equals("TAX_INCLUSIVE")) {
                        netprice = cart.getProductPrice();
                    }
                    else{
                        netprice = cart.getNetPrice();
                    }
                    double price =netprice * quantity;

                    //haris added on 04-09-2021
                    double disc_tot = cart.getProductDiscount() * quantity;
                    cart.setTotalPrice(price - disc_tot);
                    cart.setProductTotal(price - disc_tot);
                    Log.e("getUnitselected aftr",""+ cart.getUnitid_selected());
                    db_qnty_kgm=0;
//                    if (cart.getUnitid_selected().equals("10")) {
//
//                        db_qnty_kgm = cart.getTypeQuantity();
//                    }
//                    else {
//                        float confctr = TextUtils.isEmpty(cart.getConfactor_kg()) ? 0 : Float.valueOf((cart.getConfactor_kg()));
//                        Log.e("getTypeQuantity aftr",""+ cart.getTypeQuantity());
//                        Log.e("confctr aftr",""+ confctr);
//                        db_qnty_kgm = cart.getTypeQuantity()/confctr;
//                    }
                    cart.setQnty_kgm(db_qnty_kgm);
                    Log.e("txvlueee ", ""+cart.getTaxValue());
                    myDatabase.update_productdetails_byprodid(cart.getProductId(),cart.getTypeQuantity(),db_qnty_kgm);
                    updateItems(cart, position);


                    String disc = ""+st_disc;

                    Log.e("Pass Disc", ""+disc);
                    double discount_tot = 0;
                    double applicable_total = 0;
                    try {
                        if (disc.isEmpty()) {
                            disc = "0";
                        }

                        discount_tot = getDiscountTotal();
                        applicable_total = getApplicableTotal();

                        if (Double.parseDouble(disc)>=discount_tot){
                            if (applicable_total>0) {

                                double less_discount = Double.parseDouble(disc) - discount_tot;

                                Log.e("Less Disc", "" + less_discount);
                                Log.e("Applicable ", "" + applicable_total);

                                AddDiscount(less_discount, applicable_total);

                            }

                        }else {

                        }

                    } catch (Exception e) {

                    }

                    //}

                }else {
                    Toast.makeText(context, "Quantity cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void changeItem(CartItem item){
        Log.e("change top",""+item.getProductTotal());

        boolean isAdd=false;

        if (cartItems.isEmpty()) {
            addItem(item);
        }else {
//            for (int i = 0; i < cartItems.size(); i++) {
//
//                if (item.getProductId()==cartItems.get(i).getProductId()) {
//                    Log.e("confact",""+item.getCon_factor());
//                    Log.e("confact cart",""+cartItems.get(i).getCon_factor());
//
//
//                   // addItem(item);
//                   // updateItem(item, i);
//                   // isAdd=true;
//
//                }
//            }
            if (!isAdd){
                addItem(item);
            }
        }
    }

    public ArrayList<CartItem> getCartItems(){
        return cartItems;
    }
//    public double getNetTotal() {
//        double netTotal = 0;
//        return 0;
//    }

    public ArrayList<CartItem> getdeleted_items(){
        try {
            for (int i = 0; i < cartItems.size(); i++) {
                for (int j = 0; j < deleted_cartItems.size(); j++) {
                    if (cartItems.get(i).getProductId() == deleted_cartItems.get(j).getProductId()) {
                        Log.e("if position","ok");
                        deleted_cartItems.remove(j);
                    }
                    else{
                        Log.e("else position","ok");
                    }
                }
            }
        }catch (Exception e){

        }
        return deleted_cartItems;
    }

    public double getNetTotal() {


        double netTotal = 0;

        for (CartItem cartItem : cartItems) {
            Log.e("cartItems.size()",""+cartItems.size());
            double d =0;
            //if (cartItem.getNetPrice() != 0.0) {
            double netPrice =0;
            /////////////////////added o n 14/09/22/////////////////
        if(cartItem.getTax_type().equals("TAX_INCLUSIVE")) {
                netPrice = cartItem.getProductPrice();
        }
        else{
             netPrice = cartItem.getNetPrice();
        }

                cartItem.setWithouttaxTotal(netPrice*cartItem.getTypeQuantity() - cartItem.getProductDiscount()*cartItem.getTypeQuantity());
            Log.e("hrct scnddd",""+cartItem.getWithouttaxTotal());
                //  double d = cartItem.getNetPrice() * cartItem.getPieceQuantity();
                //  double d = cartItem.getProductPrice() * cartItem.getPieceQuantity();

                 d = cartItem.getWithouttaxTotal();



//            }
//            else{
//                Log.e("else ddd",""+cartItem.getNetPrice());
//                d = cartItem.getWithouttaxTotal();
//            }
            netTotal += d;

        }
        Log.e("netTotal hrhfa",""+netTotal);
        return netTotal;
    }

//    public double getWithoutTaxTotal() {
//        double netTotal = 0;
//        return 0;
//    }

    public double getWithoutTaxTotal() {


        double netTotal = 0;

        for (CartItem cartItem : cartItems) {
           // if (cartItem.getNetPrice() != 0.0) {
                Log.e("",""+cartItem.getPieceQuantity());
                Log.e("getPieceQuantity_nw",""+cartItem.getPieceQuantity_nw());
                Log.e("getProductPrice",""+cartItem.getProductPrice());
                //  double d = cartItem.getNetPrice() * cartItem.getPieceQuantity();
                double prodprice = cartItem.getProductPrice();
                if(cartItem.getTax_type().equals("TAX_INCLUSIVE")){

                     //  prodprice = cartItem.getProductTotal()/cartItem.getPieceQuantity_nw();
                    prodprice = cartItem.getProductPrice();
                }
                else{
                    prodprice = cartItem.getProductPrice()-cartItem.getProductDiscount();
                }
                double d = prodprice * cartItem.getPieceQuantity_nw();

                //   double d = cartItem.getWithouttaxTotal();

                netTotal += d;


          //  }

        }
        Log.e("netTotallllllllllll",""+netTotal);
        return netTotal;
    }

    public float get_totalkg(){
        float total_kg =0;

        return 0;
    }

//    public float get_totalkg(){
//        float total_kg =0;
//        for (CartItem cartItem : cartItems) {
//            total_kg = total_kg + cartItem.getTypeQuantity();
//        }
//        return total_kg;
//    }

//    public double getGrandTotal() {
//        double grandTotal = 0.0;
//        return 0;
//    }

    public double getGrandTotal() {

        double grandTotal = 0.0;

        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
               // if (cartItem.getTotalPrice() != 0.0) {
                    Log.e("totalprice",""+cartItem.getTotalPrice());
                    double f = cartItem.getTotalPrice();
                    grandTotal += f;
                //}
            }
        }
        return grandTotal;
    }


    public double getDiscountTotal() {
        double discTotal = 0.0;

        return 0;

    }
//    public double getDiscountTotal() {
//
//        double discTotal = 0.0;
//
//        if (!cartItems.isEmpty()) {
//            for (CartItem cartItem : cartItems) {
//                double f =0;
//                if (cartItem.getProductDiscount() != 0.0) {
//                     f = cartItem.getProductDiscount() * cartItem.getTypeQuantity();
//
//                }
//                else{
//                    f=0;
//                }
//                discTotal += f;
//            }
//        }
//        return discTotal;
//    }


    public double getApplicableTotal() {
        double applicableTotal = 0.0;
        return 0;
    }
//    public double getApplicableTotal() {
//
//        double applicableTotal = 0.0;
//
//        if (!cartItems.isEmpty()) {
//            for (CartItem cartItem : cartItems) {
//                if (cartItem.isDiscountStatus()) {
//                    double f = cartItem.getProductTotalValue();
//                    applicableTotal += f;
//                }
//            }
//        }
//        return applicableTotal;
//    }


    public void AddDiscount(double discount, double total){

        double difference = total-discount;
        Log.e("total crt", ""+total);
        Log.e("difference", ""+difference);

        if (!cartItems.isEmpty()) {
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem cartItem = cartItems.get(i);

                if (cartItem.isDiscountStatus()) {
                    double st_producttotal =0;
                    double st_taxvalue = 0;

                    double value = (cartItem.getProductTotalValue() * 100) / total;

                    double disc_value = (value * difference) / 100;

                    Log.e("disc value", ""+disc_value);


//                    cartItem.setProductTotal_withoutdisc(cartItem.getProductTotal());
//                    cartItem.setTax_valuewithoutdisc(cartItem.getTaxValue());

                    cartItem.setProductTotal(Double.parseDouble(getAmount(disc_value)));
                    cartItem.setTaxValue(getTaxPrice(disc_value, cartItem.getTax(),cartItem.getTax_type()));

                    //cartItem.setWithouttaxTotal(cartItem.getProductTotal()); //

                    Log.e("disc pro tot", ""+cartItem.getProductTotal());
                    Log.e("disc pro totwithtt", ""+cartItem.getProductTotal_withoutdisc());
                    updateItem(cartItem, i);

                }
            }
        }

    }

    public void AddDiscount_else(){



        if (!cartItems.isEmpty()) {
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem cartItem = cartItems.get(i);

                if (cartItem.isDiscountStatus()) {
                    Log.e("withoitdisc",""+cartItem.getProductTotal_withoutdisc());


//                    cartItem.setProductTotal_withoutdisc(cartItem.getProductTotal());
//                    cartItem.setTax_valuewithoutdisc(cartItem.getTaxValue());

                    cartItem.setProductTotal(cartItem.getProductTotal_withoutdisc());
                    cartItem.setTaxValue(cartItem.getTax_valuewithoutdisc());


                    Log.e("disc pro totwithtt", ""+cartItem.getProductTotal_withoutdisc());
                    updateItem(cartItem, i);

                }
            }
        }

    }
    public double getTaxTotal(String tax_type) {
        double totalTax = 0.0;

        return  0;
    }

//    public double getTaxTotal(String tax_type) {
//
//        double totalTax = 0.0;
//
//        if (!cartItems.isEmpty()) {
//            for (CartItem c : cartItems) {
//                Log.e("prodtotal",""+c.getProductTotal());
//                Log.e("prodtotal",""+c.getProductTotal());
//                Log.e("getProductTotalValue",""+c.getProductTotalValue());
////                if(tax_type.equals("TAX_INCLUSIVE")){
////                    c.setProductTotal(c.getProductTotal()+c.getTaxValue());
////                }
//                double f = getTaxPricefl(c.getProductTotalValue(), c.getTax(),c.getTax_type());
//
//                double tothalf =0;
//                if(f>0) {
//                     tothalf = Double.parseDouble(getAmount(f / 2));
//                }
//                double tx = tothalf*2;
//
//
//                totalTax += tx;
//
//            }
//        }
//        Log.e("totalTax",""+totalTax);
//        return totalTax;
//    }

//    public double getTaxTotal_new(double total_afterdisc) {
//        Log.e("total_afterdisc",""+total_afterdisc);
//
//
//        double totalTax = 0.0;
//        float tax_total_rate = 0;
//        for (CartItem c : cartItems) {
//            Log.e("prodtotal",""+c.getProductTotal());
//            double f =  c.getTax();
//            tax_total_rate += f;
//                /*if (c.getTaxValue() != 0.0) {
//                    double f = c.getTaxValue() * c.getPieceQuantity();
//                    totalTax += f;
//                }*/
//        }
//        totalTax= getTaxPrice(total_afterdisc, tax_total_rate);
//        return totalTax;
//    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class RvCartHolder extends RecyclerView.ViewHolder {

        TextView tvSlNo, tvProductName, tvQty, tvUnitPrice, tvTotal,tvCode,tvType, textView_item_free_qty, textView_item_discount;
        ImageButton ibEdit, ibDelete;


        public RvCartHolder(View itemView) {
            super(itemView);

            tvSlNo =  (TextView)itemView.findViewById(R.id.textView_item_cart_no);
            tvProductName = (TextView) itemView.findViewById(R.id.textView_item_cart_productName);
            tvQty =  (TextView)itemView.findViewById(R.id.textView_item_cart_qty);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.textView_item_cart_unitPrice);
            tvTotal =  (TextView)itemView.findViewById(R.id.textView_item_cart_total);
            tvType = (TextView) itemView.findViewById(R.id.textView_item_cart_type);
            textView_item_free_qty = (TextView) itemView.findViewById(R.id.textView_item_free_qty);
            textView_item_discount = (TextView) itemView.findViewById(R.id.textView_item_discount);

            tvCode=  (TextView)itemView.findViewById(R.id.textView_item_cart_code);

            ibDelete = (ImageButton) itemView.findViewById(R.id.imageButton_item_cart_delete);
            ibEdit = (ImageButton) itemView.findViewById(R.id.imageButton_item_cart_edit);

        }
    }
}
