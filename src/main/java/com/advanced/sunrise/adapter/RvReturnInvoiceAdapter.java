package com.advanced.minhas.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.advanced.minhas.model.Size;
import com.advanced.minhas.model.Size_Return;
import com.advanced.minhas.session.SessionValue;
import com.advanced.minhas.textwatcher.TextValidator;
import com.google.gson.Gson;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.advanced.minhas.config.AmountCalculator.getTaxPrice;
import static com.advanced.minhas.config.ConfigValue.PRODUCT_UNIT_CASE;
import static com.advanced.minhas.config.Generic.getAmount;
import static com.advanced.minhas.session.SessionValue.PREF_CURRENCY;

/**
 * Created by mentor on 30/10/17.
 */

public class RvReturnInvoiceAdapter extends RecyclerView.Adapter<RvReturnInvoiceAdapter.RvReturnProductHolder> {

    private ArrayList<CartItem> cartItems;
    Dialog dialog;
    private Context context;
    private SessionValue sessionValue;
    RecyclerView recyclerview;
    Edit_ReturnqtyAdapter sizeadapter;
    TextView tv_total, tv_totalqty;
    String st_total_qty = "", st_each_sizeqty = "", st_size_string = "", prod_code_edit ="",st_sizelist ="";
    String TAG = "RvReturnInvoiceAdapter", CURRENCY = "";
    private ArrayList<Size_Return> arr_size_afteredit = new ArrayList<>();
    private MyDatabase myDatabase;

    private ArrayList<String> sizeitems_selected_array = new ArrayList<>();
    private ArrayList<String> sizeitems_continous = new ArrayList<>();
    private ArrayList<Size_Return> array_sizefull = new ArrayList<>();
    private ArrayList<CartItem> array_size_stock = new ArrayList<>();
    private ArrayList<Size_Return> array_sizestock = new ArrayList<>();

    ArrayList<String> size_finalarray = new ArrayList<>();
    public OnNotifyListener listener;

    public RvReturnInvoiceAdapter(ArrayList<CartItem> cartItems, OnNotifyListener listener) {
        this.cartItems = cartItems;


        try {
            this.listener = listener;
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public RvReturnProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        this.context = parent.getContext();
        myDatabase = new MyDatabase(this.context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_return_invoice, parent, false);

        sessionValue = new SessionValue(parent.getContext());

        try {
            CURRENCY = "" + sessionValue.getControllSettings().get(PREF_CURRENCY);
            Log.e("Currency", "" + CURRENCY);

        } catch (Exception e) {
            Log.e("Session Error", "" + e.getMessage());
        }

        return new RvReturnProductHolder(view);
    }

    @Override
    public void onBindViewHolder(RvReturnProductHolder holder, final int position) {


        final CartItem cartItem = cartItems.get(position);
        int s = position + 1;

        holder.tvSlNo.setText(String.valueOf(s));
        holder.tvProductCode.setText(cartItem.getProductCode());
        holder.tvProductName.setText(cartItem.getProductName());

       // holder.tvActualQty.setText(String.valueOf(cartItem.getPieceQuantity()/cartItem.getCon_factor()));
        holder.tvActualQty.setText(String.valueOf(cartItem.getPieceQuantity()));

        holder.tvReturnQty.setText(String.valueOf(cartItem.getPieceQuantity_nw()));
        holder.textView_item_discount.setText(""+cartItem.getProductDiscount());
        double netPrice = cartItem.getNetPrice();
        if(cartItem.getTax_type().equals("TAX_INCLUSIVE")) {
            netPrice = cartItem.getProductTotal()/cartItem.getPieceQuantity_nw();
        }
        holder.tvNetPrice.setText(getAmount(netPrice));

        //holder.tvTotal.setText(String.valueOf(getAmount(cartItem.getNetPrice() * cartItem.getReturnQuantity()) + " " + CURRENCY));
        holder.tvTotal.setText(""+cartItem.getProductTotal());
        cartItems.get(position).setWithouttaxTotal(cartItem.getProductTotal()); //
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete(position);
                delete_dialogue(position);
            }
        });


        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);
                v.setAnimation(anim);

                 prod_code_edit = cartItem.getProductCode();
                //haris get_stocksize list


               // get_stock_sizes();
                showEditDialog(cartItem,position);
                //show_editdialogue(cartItem, position);

            }
        });


        if (listener != null)
            listener.onNotified();

    }

    private void show_editdialogue(final CartItem cart, final int position) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

//        LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dial_edit_cart_new, null);
        dialogBuilder.setView(dialogView);


        recyclerview = (RecyclerView) dialogView.findViewById(R.id.recyclerview);
        tv_total = (TextView) dialogView.findViewById(R.id.tv_total);
        tv_totalqty = (TextView) dialogView.findViewById(R.id.tv_qty);


        double netPrice = cart.getNetPrice();
        if (cart.getOrderType().equals(PRODUCT_UNIT_CASE))
            netPrice = netPrice * cart.getPiecepercart();


        // Log.e("")
        tv_total.setText("" + netPrice);

        tv_totalqty.addTextChangedListener(new TextValidator(tv_totalqty) {
            @Override
            public void validate(TextView textView, String qtyString) {

                if (!TextUtils.isEmpty(qtyString)) {

                    float quantity = TextUtils.isEmpty(qtyString) ? 0 : Float.valueOf(qtyString);

                    Log.e("cart quantity", "" + quantity);
                    Log.e("cart pieces", "" + cart.getPiecepercart());
                    Log.e("cart ordertype", "" + cart.getOrderType());

                    if (cart.getOrderType().equals(PRODUCT_UNIT_CASE))
                        quantity = quantity * cart.getPiecepercart();

                    Log.e("cart quantity if", "" + quantity);
                    if (quantity != 0) {

                        Log.e("cart if", "" + quantity);
                        tv_total.setText(getAmount(cart.getNetPrice() * quantity));
                        double db_withouttax = Double.parseDouble(getAmount(cart.getNetPrice() * quantity)) - (cart.getProductDiscount() * quantity);
                        String str_withouttax = "" + getAmount(db_withouttax);
                        //String str_withouttax = "" + getAmount(cart.getNetPrice() * quantity);
                        str_withouttax = str_withouttax.replace(",", "");

                        cartItems.get(position).setWithouttaxTotal(Double.parseDouble(str_withouttax));


                        cartItems.get(position).setProductTotal(Double.parseDouble(str_withouttax));
                        cartItems.get(position).setProductTotalValue(Double.parseDouble(str_withouttax));

                    } else {
                        Log.e("cart else", "" + quantity);
                        tv_total.setText(getAmount(cart.getNetPrice() * 0));
                        cartItems.get(position).setWithouttaxTotal(Double.parseDouble(getAmount(cart.getNetPrice() * 0)));

                        cartItems.get(position).setProductTotal(0);

                    }
                } else {
                    tv_total.setText(getAmount(cart.getNetPrice() * 0));
                    cartItems.get(position).setWithouttaxTotal(Double.parseDouble(getAmount(cart.getNetPrice() * 0)));
                }
            }
        });


        //String st_sizelistbycart = cart.getSizelist();
        String st_sizelistbycart = cart.getSizelist_foredit();
        get_sizelist(st_sizelistbycart);
        setrecycler();


        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                Log.e("cart.getStockQuantity()", "" + cart.getPieceQuantity());
                Log.e("cart.getPieceperca()", "" + cart.getPiecepercart());
                //Log.e("cart.getPieceperca()",""+cart.get());

                String str = tv_totalqty.getText().toString().trim();

                if (!str.isEmpty()) {
                    float quantity = TextUtils.isEmpty(str) ? 0 : Float.valueOf(str);

                    float check_quantity = TextUtils.isEmpty(str) ? 0 : Float.valueOf(str);
                    if (cart.getOrderType().equals(PRODUCT_UNIT_CASE)) {
                        check_quantity = quantity * cart.getPiecepercart();
                    }

                    Log.e("quantity entered", "" + check_quantity);
                    Log.e("stock qty", "" + cart.getPieceQuantity());
                    int qnty = Math.round(check_quantity);
                    if (qnty > cart.getPieceQuantity()) {
                        Log.e("Entered if", "" + cart.getPieceQuantity());
                        cartItems.get(position).setWithouttaxTotal(Double.parseDouble(getAmount(cart.getNetPrice() * 0)));
                        if (cart.getOrderType().equals(PRODUCT_UNIT_CASE)) {
                            float stock = cart.getPieceQuantity() / cart.getPiecepercart();
                            Log.e("Entered if case", "" + stock);

                            Toast.makeText(context, "Maximum Stock is " + stock, Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(context, "Maximum Stock is " + cart.getPieceQuantity(), Toast.LENGTH_SHORT).show();

                        }
                    } else {

                        int qtyy = Math.round(quantity);
                        int qty = Integer.parseInt("" + qtyy);
                        cart.setTypeQuantity(qty);

                        if (cart.getOrderType().equals(PRODUCT_UNIT_CASE))
                            quantity = quantity * cart.getPiecepercart();

                        cart.setReturnQuantity(qty);
                        cart.setTypeQuantity(qty);
                        cart.setTotalPrice(cart.getSalePrice() * quantity);

                        double disc_tot = cart.getProductDiscount() * quantity;

//                        cart.setTotalPrice(price - cart.getProductDiscount());
//                        cart.setProductTotal(price - cart.getProductDiscount());
                        double price = cart.getNetPrice() * quantity;
                        cart.setTotalPrice(price - disc_tot);
                        cart.setProductTotal(price - disc_tot);
                        Log.e("orodtothr",""+(cart.getProductTotal()));
                        updateItem(cart, position);

                        Log.e("pieceqty", "" + cart.getPieceQuantity());
                        //haris added on 30-10-2020
                        ArrayList<Size_Return> size_array = new ArrayList<>();
                        for (Size_Return s : arr_size_afteredit) {
                            s.setSizeId(s.getSizeId());
                            int sizeqty = Math.round(s.getSize_after_edit());
                            Log.e("sizeqty", "" + sizeqty);
                            s.setQuantity("" + sizeqty); //sale affected stock

                            size_array.add(s);
                        }
                        //haris
//                        String gsonvalue_size = new Gson().toJson(size_array);
//                        cart.setSizelist(gsonvalue_size);
//                        Log.e("gsonvalue_size", "" + gsonvalue_size);
                        //
                        String gsonvalue_size=update_sizeand_quantity_aftresalenew();
                        cart.setSizelist(gsonvalue_size);
                        Log.e("gsonvalue_sizejan", "" + gsonvalue_size);

                        try {
                            String st_main = get_selected_sizes();

                            int numb = Integer.parseInt(st_main);
                            sizeitems_selected_array.clear();
                            String number = String.valueOf(numb);

                            for (int i = 0; i < number.length(); i++) {
                                int j = Character.digit(number.charAt(i), 10);
                                Log.e("nos", "" + j);


                                sizeitems_selected_array.add("" + j);
                            }
                        } catch (Exception e) {

                        }
                        set_sizes();

                        st_each_sizeqty = st_each_sizeqty.substring(0, st_each_sizeqty.length() - 1);
                        Log.e("st_each_sizeqty hr", "" + st_each_sizeqty);
                        cart.setSizeandqty_string(st_each_sizeqty);
                        cart.setSize_string(st_size_string);
                    }


                } else {
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

        boolean isAdd=false;
        if (cartItems.isEmpty()) {
            addItem(item);
        }else {
            for (int i = 0; i < cartItems.size(); i++) {

                if (item.getProductId()==cartItems.get(i).getProductId()) {

                    //           if (item.equals(cartItems.get(i))) {
/*    // ------------ %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% -----------------

                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setMessage("Product already added ! Please edit from list if needed");

                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                           alertDialog.dismiss();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();

   // ------------------- %%%%%%%%%%%%%%%%%%%%%% --------------*/

                    updateItem(item, i);
                    isAdd=true;

                }
            }
            if (!isAdd){
                addItem(item);
            }
        }
    }

    private String update_sizeand_quantity_aftresalenew() {

        String gsonvalue_siz = "";
        int if_flag = 0, else_flag = 0;

        ArrayList<Size_Return> size_array = new ArrayList<>();
        ArrayList<String> sizearray_string = new ArrayList<>();

        int st_sizeid = 0;
        int newqnty = 0;
        size_array.clear();
        Log.e("array_sizestocksz",""+array_sizestock.size());
        Log.e("array_s_aftersalesz",""+arr_size_afteredit.size());
        int flag_check = 0;

        for (Size_Return s : array_sizestock) {
            flag_check =0;
            for (Size_Return r : arr_size_afteredit) {

                if (s.getSizeId().equals(r.getSizeId())) {

                    Log.e("getTotal_qty", "" + r.getTotal_qty());

                    if (!r.getTotal_qty().equals("0")) {
                        Log.e("hrin", "" + r.getTotal_qty());
                        //float no = Float.parseFloat(r.getTotal_qty());
                        //int qnty = Math.round(no);

                        flag_check=1; //check same size get or not
                        newqnty = Integer.parseInt(r.getTotal_qty()) + Integer.parseInt(s.getQuantity());

                        else_flag = 0;
                        if_flag = 1;
                        st_sizeid = r.getSizeId();



                    } else {
                        Log.e("sze else s", "" + s.getSizeId());


                        if_flag = 0;
                        else_flag = 1;

                    }
                } else {

                    if(flag_check == 0) {
                        if_flag = 0;
                        else_flag = 1;
                    }

                }


            }

            Size_Return n = new Size_Return();
            if (if_flag == 1) {
                if_flag = 0;
                else_flag = 0;
                Log.e("if_flag", "" + newqnty);
                n.setSizeId(st_sizeid);
                n.setQuantity("" + newqnty); //sale affected stock
                n.setNew_qnty_aftersale(s.getNew_qnty_aftersale());
                n.setSize_stock("" + s.getReturn_qnty()); //sale not affected stock
                //n.setSize_after_edit(Float.parseFloat(s.getReturn_qnty())); //for edit after cart add
                n.setTotal_qty(s.getTotal_qty()); //for edit after cart add
//
//                        //s.setAvailable_stock(int_product_qty);
//                        size_array.add(n);

                sizearray_string.add(""+s.getSizeId());
                sizearray_string.add(""+newqnty);

                st_sizeid = 0;
                newqnty = 0;

            }
            if (else_flag == 1) {
                if_flag = 0;
                else_flag = 0;
                Log.e("else_flag", "" + s.getQuantity());
                n.setSizeId(s.getSizeId());
                n.setQuantity("" + s.getQuantity()); //sstock qty
                n.setNew_qnty_aftersale(s.getNew_qnty_aftersale());
                n.setSize_stock("" + s.getReturn_qnty()); //sale not affected stock
                // n.setSize_after_edit(Float.parseFloat(s.getReturn_qnty())); //for edit after cart add
                n.setTotal_qty(s.getTotal_qty()); //for edit after cart add
//                    size_array.add(n);

                sizearray_string.add(""+s.getSizeId());
                sizearray_string.add(""+s.getQuantity());
            }
            size_array.add(n);


        }

        gsonvalue_siz = new Gson().toJson(size_array);
        Log.e("gsonvalue_siz", gsonvalue_siz);
        Log.e("sizearray_string",""+sizearray_string);


        return gsonvalue_siz;

    }


    private void set_sizes() {
        //Log.e("list sz", "" + sizeitems_selected_array.size());
        //Log.e("list 1", "" + sizeitems_selected_array);
        String first = "", second = "", st_fullstring = "";
        int flag = 0;
        int full_flag = 0;
        size_finalarray.clear();

        ArrayList<String> arr_new = new ArrayList<>();
        arr_new.clear();
        try {
            for (int i = 0; i < sizeitems_selected_array.size(); i++) {
                //Log.e("list", "" + sizeitems_selected_array.get(i));
                if (Integer.parseInt(sizeitems_selected_array.get(i + 1)) == (Integer.parseInt(sizeitems_selected_array.get(i))) + 1) {

                    flag = 1;

                    if (first.equals("")) {
                        first = sizeitems_selected_array.get(i);
                    } else {

                        Log.e("removee haris 2", "" + sizeitems_selected_array.get(i));
                        arr_new.add(sizeitems_selected_array.get(i));

                    }
                    if ((i + 1) == sizeitems_selected_array.size() - 1) {
//                        Log.e("size", "" + sizeitems_selected_array.size());
//                        Log.e("i+1", "" + (i+1));
                        full_flag = 1;
                    }

                } else {

                    full_flag = 0;

                    if (flag == 1) {

                        st_fullstring = first + "-" + sizeitems_selected_array.get(i);
                        size_finalarray.add(st_fullstring);
                        sizeitems_continous.add(first);
                        sizeitems_continous.add(sizeitems_selected_array.get(i));
                        first = "";
                        flag = 0;

                    } else {


                    }

                }

                if (full_flag == 1) {
                    st_fullstring = first + "-" + sizeitems_selected_array.get(i + 1);
                    size_finalarray.add(st_fullstring);
                    sizeitems_continous.add(first);
                    sizeitems_continous.add(sizeitems_selected_array.get(i + 1));
                    first = "";
                    flag = 0;
                    full_flag = 0;
                }


            }
        } catch (Exception e) {

        }
//        Log.e("sizeitems_continous", "" + sizeitems_continous);
//
//        Log.e("size_finalarray", "" + size_finalarray);
        Log.e("arr_new", "" + arr_new);
        Log.e("sizeite_array", "" + sizeitems_selected_array);
        Log.e("continarray", "" + sizeitems_continous);
        try {
            for (int i = 0; i < sizeitems_selected_array.size(); i++) {

                for (int j = 0; j < arr_new.size(); j++) {
                    if (sizeitems_selected_array.get(i) == arr_new.get(j)) {
                        sizeitems_selected_array.remove(i);
                    }
                }
            }
        } catch (Exception e) {

        }
        Log.e("sizeitselec_array last", "" + sizeitems_selected_array);
        get_odd_size();

    }

    private void get_odd_size() {
        try {


            for (int i = 0; i < sizeitems_selected_array.size(); i++) {
                if (sizeitems_continous.contains(sizeitems_selected_array.get(i))) {
                    //do something for equals
                } else {
                    //do something for not equals
                    int index = sizeitems_continous.indexOf(sizeitems_selected_array.get(i));
                    //B_arraylist.remove(index);
                    // Log.e("not match", "" + sizeitems_selected_array.get(i));

                    //sizeitems_continous_odd.add(sizeitems_selected_array.get(i));
                    size_finalarray.add(sizeitems_selected_array.get(i));
                }
            }

        } catch (Exception e) {

        }

        st_size_string = "";
        for (int i = 0; i < size_finalarray.size(); i++) {
            st_size_string = st_size_string + size_finalarray.get(i) + ",";

        }
        if (st_size_string.length() > 0)
            st_size_string = st_size_string.substring(0, st_size_string.length() - 1);

        Log.e("st_size_string", "" + st_size_string);

    }

    private String get_selected_sizes() {

        String st_selected_size = "";
        String size_sel = "";
        st_each_sizeqty = "";

        for (Size_Return s : arr_size_afteredit) {
            Log.e("sizes", "hr " + s.getSelected_sizes());

            try {
                if (Integer.parseInt(s.getSelected_sizes()) > 0) {
                    if (!s.getTotal_qty().equals("0")) {
                        if (!s.getTotal_qty().equals("0.0")) {
                            st_selected_size = st_selected_size + Integer.parseInt("" + s.getSelected_sizes());
                            size_sel = s.getSelected_sizes();
                            //total_size_array.add(s.getSelected_sizes());
                        }
                    }
                }
            } catch (Exception e) {

            }
            try {
                //total_eachsize_qty_array
                if (!s.getTotal_qty().equals("0")) {
                    if (!s.getTotal_qty().equals("0.0")) {
                        int each_szeqty = Math.round(Float.parseFloat(s.getTotal_qty()));
                        st_each_sizeqty = st_each_sizeqty + s.getSelected_sizes() + "-" + each_szeqty + ",";
                    }
                }

            } catch (Exception e) {

            }

        }
        Log.e("getSelected_sizes", "" + st_selected_size);
        Log.e("st_each_sizeqty", "" + st_each_sizeqty);
        return st_selected_size;

    }

    private void setrecycler() {

        st_total_qty = "";
        // sizeadapter = new Edit_SaleqtyAdapter(arr_sizelist1, new OnNotifyListener() {
        sizeadapter = new Edit_ReturnqtyAdapter(array_sizefull, new OnNotifyListener() {
            @Override
            public void onNotified() {
                arr_size_afteredit.clear();

                arr_size_afteredit = sizeadapter.get_sizequantity_array();
                Log.e("arr_size_afteredit", "" + arr_size_afteredit);
                for(Size_Return r : arr_size_afteredit){
                    Log.e("getTotal_qty", "" + r.getTotal_qty());
                    Log.e("getSize_after_edit", "" + r.getSize_after_edit());
                    Log.e("getSizeId", "" + r.getSizeId());
                }
                try {
                    if (arr_size_afteredit.size() > 0) {

                        st_total_qty = sizeadapter.getTotal_quantity();
                        Log.e("st_total_wty", "" + st_total_qty);

                        tv_totalqty.setText("" + st_total_qty);
                    }
                } catch (Exception e) {

                }

            }
        });


        try {

            recyclerview.setHasFixedSize(true);
            recyclerview.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context)
                    .showLastDivider()
                    .build());
            recyclerview.setLayoutManager(new LinearLayoutManager(context));
            recyclerview.setAdapter(sizeadapter);

        } catch (Exception e) {

        }

    }

    public ArrayList<Size_Return> get_new_qnty_afteredit(){

        return arr_size_afteredit;
    }

    private void get_sizelist(String st_sizelistbycart) {

        try {

            JSONArray arr_size = new JSONArray(st_sizelistbycart);

            array_sizefull.clear();

            for (int i = 0; i < arr_size.length(); i++) {

                Size_Return size = new Size_Return();

                JSONObject jObj = arr_size.getJSONObject(i);
                Integer size_st = jObj.getInt("sizeId");
                String qnty = jObj.getString("quantity");
                String available_qty = jObj.getString("available_stock");
                String size_stock = jObj.getString("size_stock");
                String total_qty = jObj.getString("total_qty");

                Size_Return s = new Size_Return();
                //s.setQuantity(qnty);
                s.setSizeId(size_st);
                s.setTotal_qty(total_qty);
                s.setSelected_sizes("");
                s.setSize_after_edit(Float.parseFloat(qnty));
                s.setSize_stock(size_stock);
                array_sizefull.add(s);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void delete_dialogue(final int pos) {
        {

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialogue_sale_deleteconfirm);

            android.widget.Button bt_yes;


            android.widget.Button bt_no;


            bt_no = dialog.findViewById(R.id.button_delete_Dialog_cancel);
            bt_yes = dialog.findViewById(R.id.button_delete_Dialog_ok);


            dialog.setTitle("Product Details");
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations_SmileWindow;//R.style.DialogAnimations_SmileWindow;
            dialog.setCancelable(false);

            bt_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    delete(pos);


                }
            });


            dialog.show();


        }
    }


    public ArrayList<CartItem> getReturnItems() {
        return cartItems;
    }


//total without tax amount


    public double getNetTotal() {

        double netTotal = 0;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getNetPrice() != 0.0) {
               // double d = cartItem.getNetPrice() * cartItem.getPieceQuantity_nw();//by confactr
                double d = cartItem.getWithouttaxTotal();
                netTotal += d;
            }
        }
        return netTotal;
    }

    public double getDiscountTotal() {

        double discTotal = 0.0;

        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                if (cartItem.getProductDiscount() != 0.0) {
                    double f = cartItem.getProductDiscount() * cartItem.getPieceQuantity_nw();
                    discTotal += f;
                }
            }
        }
        return discTotal;
    }


    public double getWithoutTaxTotal() {

        double netTotal = 0;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getNetPrice() != 0.0) {
                //  double d = cartItem.getNetPrice() * cartItem.getPieceQuantity();
               // double d = cartItem.getProductPrice() * cartItem.getPieceQuantity();
                double prodprice = cartItem.getProductPrice();
                if(cartItem.getTax_type().equals("TAX_INCLUSIVE")){

                    prodprice = cartItem.getProductTotal()/cartItem.getPieceQuantity_nw();
                }
                double d = prodprice * cartItem.getPieceQuantity_nw();
                //   double d = cartItem.getWithouttaxTotal();

                netTotal += d;
            }
        }
        return netTotal;
    }
    //total with tax amount
    public double getGrandTotal() {

        double grandTotal = 0.0;

        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                if (cartItem.getTotalPrice() != 0.0) {
                    double f = cartItem.getTotalPrice();
                    grandTotal += f;
                }
            }
        }
        return grandTotal;
    }


//    public double getTaxTotal() {
//        double totalTax = 0.0;
//
//        if (!cartItems.isEmpty()) {
//            for (CartItem c : cartItems) {
//                if (c.getTaxValue() != 0.0) {
//                    Log.e("taxvalue hr",""+c.getTaxValue());
//                    double f = c.getTaxValue() * c.getReturnQuantity();
//                    totalTax += f;
//                }
//            }
//        }
//        return totalTax;
//    }

    public double getTaxTotal() {
        double totalTax = 0.0;

        if (!cartItems.isEmpty()) {


            for (CartItem c : cartItems) {
                if (c.getTaxValue() != 0.0) {
                    Log.e("prodtotal",""+c.getProductTotal());
                    Log.e("getProductTotalValue",""+c.getProductTotalValue());
                    Log.e("retqnty",""+c.getReturnQuantity());
                    double f = getTaxPrice(c.getProductTotalValue(), c.getTax(),c.getTax_type());

                    // double f = c.getTaxValue() * c.getReturnQuantity();
                    totalTax += f;
                }
            }
        }
        return totalTax;
    }

    //get stock size and quantity
    private void get_stock_sizes()
    {

        String qnty="";
        try {

            JSONArray arr_size = new JSONArray(st_sizelist);

            array_sizestock.clear();


            for (int i =0;i<arr_size.length(); i++){

                Size size = new Size();

                JSONObject jObj = arr_size.getJSONObject(i);
                Integer size_st = jObj.getInt("sizeId");
                qnty = jObj.getString("quantity");
                String available_qty = jObj.getString("available_stock");

                Size_Return s = new Size_Return();
                s.setQuantity(qnty);
                s.setSizeId(size_st);
                s.setTotal_qty("0");
                s.setSelected_sizes("");
                s.setSize_after_edit(Float.parseFloat(qnty));
                array_sizestock.add(s);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("qnty  ddd",""+qnty);

    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }


 /*   private void delete(int position) { //removes the row

        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());
    }*/

    private void delete(int position) { //removes the row

        try {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());

            if (cartItems.size() == position) {
                if (listener != null)
                    listener.onNotified();
            }
        } catch (IndexOutOfBoundsException e) {
            Log.v(TAG, "delete   Exception " + e.getMessage());
        }
    }

    private void addItem(CartItem item) {
        cartItems.add(item);
//        notifyItemInserted(cartItems.size() - 1);
        notifyDataSetChanged();


    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }


    public void updateItem(CartItem item, int pos) {
        cartItems.set(pos, item);
        notifyItemChanged(pos);
    }

    public void returnItem(CartItem item) {

        boolean isAdd = false;
        if (cartItems.isEmpty()) {
            addItem(item);
            Log.d(TAG, "returnItem  empty " + item.getProductName());
        } else {
            for (int i = 0; i < cartItems.size(); i++) {

                if (item.equals(cartItems.get(i))) {
                    updateItem(item, i);
                    isAdd = true;
                    Log.d(TAG, "returnItem  update " + item.getProductName());
                }
            }
            if (!isAdd) {
                addItem(item);
                Log.d(TAG, "returnItem  add " + item.getProductName());
                isAdd = true;
            }
        }
    }


    private void showEditDialog(final CartItem cart, final int position) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

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
        tvNetPrice.setText(getAmount(cart.getNetPrice()));
        tvTotalPrice.setText(getAmount((cart.getNetPrice() * cart.getPieceQuantity_nw())-(cart.getProductDiscount()*cart.getPieceQuantity_nw())));
        etQuantity.setText(String.valueOf(cart.getPieceQuantity_nw()));

//        etQuantity.addTextChangedListener(new TextValidator(etQuantity) {
//            @Override
//            public void validate(TextView textView, String qtyString) {
//
//                if (!TextUtils.isEmpty(qtyString)) {
//
//                    int quantity = TextUtils.isEmpty(qtyString) ? 0 : Integer.valueOf(qtyString);
//
//                    Log.e("cart quantity", "" + quantity);
//                    Log.e("cart pieces", "" + cart.getPiecepercart());
//
//                    Log.e("cart ordertype", "" + cart.getOrderType());
//
//                    if (cart.getOrderType().equals("2"))
//                        quantity = quantity * cart.getPiecepercart();
//
//                    cart.setPieceQuantity(quantity);
//                    // Log.e("cart quantity", ""+quantity);
//                    if (quantity != 0) {
//
//                        Log.e("cart if", "" + quantity);
//                        tvTotalPrice.setText(getAmount(cart.getNetPrice() * quantity));
//                    } else {
//                        Log.e("cart else", "" + quantity);
//                        tvTotalPrice.setText(getAmount(cart.getNetPrice() * 0));
//
//                    }
//                } else {
//                    tvTotalPrice.setText(getAmount(cart.getNetPrice() * 0));
//                }
//            }
//        });
        etQuantity.addTextChangedListener(new TextValidator(etQuantity) {
            @Override
            public void validate(TextView textView, String qtyString) {

                if (!TextUtils.isEmpty(qtyString)) {

                    float quantity = TextUtils.isEmpty(qtyString) ? 0 : Float.valueOf(qtyString);

                    Log.e("cart quantity", ""+quantity);
                    Log.e("cart pieces", ""+cart.getPiecepercart());

                    Log.e("cart ordertype", ""+cart.getOrderType());

                    if (cart.getOrderType().equals(PRODUCT_UNIT_CASE))
                        quantity = quantity * cart.getPiecepercart();

                    // Log.e("cart quantity", ""+quantity);
                    if (quantity != 0) {

                        Log.e("cart if", ""+quantity+"/price/"+cart.getNetPrice());
                        //  tvTotalPrice.setText(getAmount(cart.getNetPrice() * quantity));
                        tvTotalPrice.setText(getAmount(cart.getNetPrice() * quantity - (cart.getProductDiscount() *quantity)));

//                            double db_withouttax = Double.parseDouble(getAmount(cart.getNetPrice() * quantity)) - cart.getProductDiscount();

                        double db_withouttax = Double.parseDouble(getAmount(cart.getNetPrice() * quantity)) - (cart.getProductDiscount() * quantity);

                        //  String str_withouttax = ""+getAmount(cart.getNetPrice() * quantity);
                        String str_withouttax = "" + getAmount(db_withouttax);
                        str_withouttax = str_withouttax.replace(",", "");

                        cartItems.get(position).setWithouttaxTotal(Double.parseDouble(str_withouttax));
                        cartItems.get(position).setProductTotal(Double.parseDouble(str_withouttax));
                        cartItems.get(position).setProductTotalValue(Double.parseDouble(str_withouttax));
                    } else {
                        Log.e("cart else", ""+quantity);

                        tvTotalPrice.setText(getAmount(cart.getNetPrice() * 0));
                        cartItems.get(position).setWithouttaxTotal(Double.parseDouble(getAmount(cart.getNetPrice() * 0)));
                        cartItems.get(position).setProductTotal(0);
                    }
                } else {
                    tvTotalPrice.setText(getAmount(cart.getNetPrice() * 0));
                }
            }
        });

        ibQuantityDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    String text = etQuantity.getText().toString().trim();
                    int previousSize = 0;

                    if (!text.isEmpty())
                        previousSize = Integer.parseInt(text);

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

                    int nextSize = 0;

                    if (!text.isEmpty())
                        nextSize = Integer.parseInt(text);

                    /**increment numbers */

                    nextSize++;
                    etQuantity.setText(String.valueOf(nextSize));

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                String str = etQuantity.getText().toString().trim();

                float quantity = TextUtils.isEmpty(str) ? 0 : Float.valueOf(str);

                if (cart.getPieceQuantity() >= quantity) {

                    //  cart.setTypeQuantity(quantity);
                    cart.setReturnQuantity(quantity);
                    cart.setPieceQuantity_nw(quantity);

                    if (cart.getOrderType().equals("2"))
                        quantity = quantity * cart.getPiecepercart();

                    double price = cart.getNetPrice() * quantity;

                    //haris added on 04-09-2021
                    double disc_tot = cart.getProductDiscount() * quantity;
                    cart.setTotalPrice(price - disc_tot);
                    cart.setProductTotal(price - disc_tot);

                    //cart.setTotalPrice(cart.getSalePrice() * quantity);
                    updateItem(cart, position);
                } else {
                    Toast.makeText(context, "Return quantity cannot be greater than entered quantity", Toast.LENGTH_SHORT).show();
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


    class RvReturnProductHolder extends RecyclerView.ViewHolder {


        TextView tvSlNo, tvProductCode, tvProductName, tvNetPrice, tvActualQty, tvReturnQty, tvTotal , textView_item_discount;
        ImageButton ibDelete, ibEdit;

        RvReturnProductHolder(View itemView) {
            super(itemView);

            tvSlNo = (TextView) itemView.findViewById(R.id.textView_item_invoice_slNo);
            tvProductCode = (TextView) itemView.findViewById(R.id.textView_item_invoice_productCode);
            tvProductName = (TextView) itemView.findViewById(R.id.textView_item_invoice_productName);
            tvNetPrice = (TextView) itemView.findViewById(R.id.textView_item_invoice_unitPrice);
            tvActualQty = (TextView) itemView.findViewById(R.id.textView_item_invoice_actualQty);
            tvReturnQty = (TextView) itemView.findViewById(R.id.textView_item_invoice_returnQty);
            tvTotal = (TextView) itemView.findViewById(R.id.textView_item_invoice_totalPrice);
            textView_item_discount = (TextView) itemView.findViewById(R.id.textView_item_discount);
            ibDelete = (ImageButton) itemView.findViewById(R.id.imageButton_item_cart_delete);
            ibEdit = (ImageButton) itemView.findViewById(R.id.imageButton_item_cart_edit);

        }
    }
}
