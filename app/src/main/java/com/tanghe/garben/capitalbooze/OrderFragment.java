package com.tanghe.garben.capitalbooze;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class OrderFragment extends Fragment {

    private final static String TAG = "Order";

    protected static double totalPrice = 0.00;
    protected static double totalPriceLast = 0.00;
    protected static int totalSquares = 0;
    protected static int totalSquaresLast = 0;
    protected static int totalCount = 0;
    protected static int totalCountLast = 0;
    protected static long maxOrder = 0L;
    protected static long allTimeWolf = 0l;

    private static LinearLayout verticalLayoutOrders;
    private static TextView mTotalPrice;
    private static TextView mTotalPriceLast;
    private static TextView mTotalSquares;
    private static TextView mTotalSquaresLast;
    private static TextView mTotalCount;
    private static TextView mTotalCountLast;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        verticalLayoutOrders = (LinearLayout) view.findViewById(R.id.verticalLayoutOrders);
        for (DrinkUI i : DrinkUI.uidrinks) {
            try {
                verticalLayoutOrders.addView(i.horizontalLayoutOrders);
            } catch (IllegalStateException e) {
                Log.d(TAG, "DrinkUI " + i.name + " already in verticalLayoutOrders");
            }
        }

        mTotalPrice = (TextView) view.findViewById(R.id.mTotalPrice);
        mTotalPriceLast = (TextView) view.findViewById(R.id.mTotalPriceLast);
        mTotalSquares = (TextView) view.findViewById(R.id.mTotalSquares);
        mTotalSquaresLast = (TextView) view.findViewById(R.id.mTotalSquaresLast);
        mTotalCount = (TextView) view.findViewById(R.id.mTotalCount);
        mTotalCountLast = (TextView) view.findViewById(R.id.mTotalCountLast);

        setTotals();
        setTotalsLast();

        final Button order = (Button) view.findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalCount > 0) {
                    for (DrinkUI i : DrinkUI.uidrinks) {
                        i.countCurrent += i.orderCount;
                        MainActivity.myRef.child("Drinks").child(i.name).child("countCurrent").setValue(i.countCurrent);

                        i.partyCount += i.orderCount;
                        MainActivity.myRef.child("Drinks").child(i.name).child("partyCount").setValue(i.partyCount);

                        i.partyRevenue += i.orderCount * i.price;
                        MainActivity.myRef.child("Drinks").child(i.name).child("partyRevenue").setValue(i.partyRevenue);

                        Drink.partyRevenueTotal += i.orderCount * i.price;
                        MainActivity.myRef.child("partyRevenueTotal").setValue(Drink.partyRevenueTotal);

                        i.orderCount = 0;
                        i.mDrinkCountOrders.setText(String.format(Locale.getDefault(), "%1d", i.orderCount));
                    }

                    Drink.countTotalCurrent += totalCount;
                    MainActivity.myRef.child("countTotalCurrent").setValue(Drink.countTotalCurrent);
                    Drink.partyCountTotal += totalCount;
                    MainActivity.myRef.child("partyCountTotal").setValue(Drink.partyCountTotal);

                    totalPriceLast = totalPrice;
                    totalPrice = 0.00;
                    totalSquaresLast = totalSquares;
                    totalSquares = 0;
                    totalCountLast = totalCount;
                    if (totalCount > maxOrder) {
                        MainActivity.myRef.child("maxOrder").setValue(totalCount);
                        if (totalCount > allTimeWolf) {
                            MainActivity.myRef.child("allTimeWolf").setValue(totalCount);
                            Toast.makeText(getContext(), getString(R.string.new_all_time_wolf), Toast.LENGTH_LONG).show();
                            Log.d(TAG, getString(R.string.new_all_time_wolf));
                        } else {
                            Toast.makeText(getContext(), getString(R.string.new_wolf), Toast.LENGTH_LONG).show();
                            Log.d(TAG, getString(R.string.new_wolf));
                        }
                    }
                    totalCount = 0;

                    setTotals();
                    setTotalsLast();
                } else {
                    Toast.makeText(getContext(), getString(R.string.nothing_to_order), Toast.LENGTH_LONG).show();
                    Log.d(TAG, getString(R.string.nothing_to_order));
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        verticalLayoutOrders.removeAllViews();
        super.onDestroyView();
    }

    public static void setTotals() {
        if (totalPrice > 0.00) {
            mTotalPrice.setText(String.format(Locale.getDefault(), "€%.2f", totalPrice));
        } else {
            totalPrice = 0.00;
            mTotalPrice.setText(String.format(Locale.getDefault(), "€%.2f", totalPrice));
        }
        mTotalSquares.setText(String.format(Locale.getDefault(), "#%1d", totalSquares));
        mTotalCount.setText(String.format(Locale.getDefault(), "%1d", totalCount));
    }

    public static void setTotalsLast() {
        mTotalPriceLast.setText(String.format(Locale.getDefault(), "(€%.2f)", totalPriceLast));
        mTotalSquaresLast.setText(String.format(Locale.getDefault(), "(#%1d)", totalSquaresLast));
        mTotalCountLast.setText(String.format(Locale.getDefault(), "(%1d)", totalCountLast));
    }
}