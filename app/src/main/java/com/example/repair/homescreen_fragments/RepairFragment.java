package com.example.repair.homescreen_fragments;


import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.repair.R;
import com.example.repair.app.GPSTracker;
import com.example.repair.pojo.RepairItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RepairFragment extends Fragment {






    private ConstraintLayout location;
    private  BottomSheetDialog dialog;
    private TextInputEditText bottomSheetPincode;
    private TextView currentLocation;

    private static final Pincode PINCODE = new Pincode();

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("REPAIRAPP",0);
        editor = sharedPreferences.edit();
        String pin = sharedPreferences.getString("PINCODE",null);
        if(pin == null) {

            enableLocation();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.repair_fragment, container, false);




        ArrayList<RepairItem> appliances = new ArrayList<>();
        appliances.add(new RepairItem("AC",R.drawable.ac_repair));
        appliances.add(new RepairItem("GEYSER",R.drawable.geyser));
        appliances.add(new RepairItem("MICROWAVE OVEN",R.drawable.microwave_oven));
        appliances.add(new RepairItem("REFRIGERATOR",R.drawable.refrigerator));
        appliances.add(new RepairItem("TV",R.drawable.tv_installation));
        appliances.add(new RepairItem("WASHING MACHINE",R.drawable.washing_machine));
        appliances.add(new RepairItem("WATER PURIFIER",R.drawable.water_purifier));

        ArrayList<RepairItem> computer = new ArrayList<>();
        computer.add(new RepairItem("DESKTOP",R.drawable.desk_software_issues));
        computer.add(new RepairItem("MAC BOOK",R.drawable.mac_software_issues));
        computer.add(new RepairItem("LAPTOP",R.drawable.lap_display_issues));
        computer.add(new RepairItem("IMAC",R.drawable.imac_display_issues));
        computer.add(new RepairItem("NETWORK SETUP",R.drawable.ns_router_installation));

        ArrayList<RepairItem> electrical = new ArrayList<>();
        electrical.add(new RepairItem("CEILING FAN", R.drawable.e2e_ceiling_fan));
        electrical.add(new RepairItem("EXHAUST FAN", R.drawable.e2e_exhuast_fan));
        electrical.add(new RepairItem("INVERTER", R.drawable.e2e_inverter));
        electrical.add(new RepairItem("SMART HOME", R.drawable.e2e_smart_home));
        electrical.add(new RepairItem("SOCKETS AND HOLDERS", R.drawable.e2e_sockets___holders));
        electrical.add(new RepairItem("WIRING", R.drawable.e2e_wiring));
        electrical.add(new RepairItem("3 PHASE PANEL BOARD", R.drawable.e2e_3_phase_board));
        electrical.add(new RepairItem("MAIN CONTROL BOARD", R.drawable.e2e_mcb));
        electrical.add(new RepairItem("NEW ELECTRIC POINT", R.drawable.e2e_new_electric_point));

        ArrayList<RepairItem> carpentry = new ArrayList<>();
        carpentry.add(new RepairItem("LOCK", R.drawable.e2e1_lock));
        carpentry.add(new RepairItem("DOOR CHAIN", R.drawable.e2e_door_chain));
        carpentry.add(new RepairItem("DOOR STOPPER", R.drawable.e2e_door_stopper));
        carpentry.add(new RepairItem("BED", R.drawable.e2e_gc_bed));
        carpentry.add(new RepairItem("MESH", R.drawable.e2e_gc_mesh));
        carpentry.add(new RepairItem("HANDLE", R.drawable.e2e_handle));
        carpentry.add(new RepairItem("HINGES", R.drawable.e2e_hinges));
        carpentry.add(new RepairItem("DOOR LATCH", R.drawable.e2e_latch));
        carpentry.add(new RepairItem("NEW SOFA", R.drawable.e2e_making_a_new_sofa));
        carpentry.add(new RepairItem("NEW WOODEN CHAIR", R.drawable.e2e_wooden_chair));
        carpentry.add(new RepairItem("WOODEN PARTITION", R.drawable.e2e_wooden_partition));



        ArrayList<RepairItem> plumbing = new ArrayList<>();
        plumbing.add(new RepairItem("WATER FILTERS", R.drawable.e2e_bathroom_water_filters));
        plumbing.add(new RepairItem("PIPELINES AND PUMPS", R.drawable.e2e_pipelines___pumps));
        plumbing.add(new RepairItem("TAP", R.drawable.e2e_tap));
        plumbing.add(new RepairItem("WASH BASIN", R.drawable.e2e_wash_basin));
        plumbing.add(new RepairItem("WATER TANK", R.drawable.e2e_water_tank));



        location = view.findViewById(R.id.SHOP_ADDRESS);
        currentLocation = view.findViewById(R.id.current_location);






        RecyclerView appliances_ = view.findViewById(R.id.appliances_recycler_view);
        appliances_.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        appliances_.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        appliances_.setItemAnimator(new DefaultItemAnimator());
        appliances_.setAdapter(new RepairItemAdapter(getActivity(), appliances, PINCODE, "APPLIANCES"));
        appliances_.setNestedScrollingEnabled(false);


        RecyclerView computer_ = view.findViewById(R.id.computer_repair_recycler_view);
        computer_.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        computer_.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        computer_.setItemAnimator(new DefaultItemAnimator());
        computer_.setAdapter( new RepairItemAdapter(getActivity(), computer, PINCODE, "COMPUTER_REPAIR"));
        computer_.setNestedScrollingEnabled(false);



        RecyclerView electrical_ = view.findViewById(R.id.electrical_recycler_view);
        electrical_.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        electrical_.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        electrical_.setItemAnimator(new DefaultItemAnimator());
        electrical_.setAdapter( new RepairItemAdapter(getActivity(), electrical, PINCODE, "ELECTRICAL"));
        electrical_.setNestedScrollingEnabled(false);

        RecyclerView carpentry_ = view.findViewById(R.id.carpentry_recycler_view);
        carpentry_.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        carpentry_.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        carpentry_.setItemAnimator(new DefaultItemAnimator());
        carpentry_.setAdapter( new RepairItemAdapter(getActivity(), carpentry, PINCODE, "CARPENTRY"));
        carpentry_.setNestedScrollingEnabled(false);

        RecyclerView plumbing_ = view.findViewById(R.id.plumbing_recycler_view);
        plumbing_.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        plumbing_.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        plumbing_.setItemAnimator(new DefaultItemAnimator());
        plumbing_.setAdapter( new RepairItemAdapter(getActivity(), plumbing, PINCODE, "PLUMBING"));
        plumbing_.setNestedScrollingEnabled(false);


        View bottomSheetView = getLayoutInflater().inflate(R.layout.location_bottom_sheet, null);
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(bottomSheetView);
        bottomSheetPincode = bottomSheetView.findViewById(R.id.bottom_pincode);
        bottomSheetView.findViewById(R.id.done).setOnClickListener(v -> {
            PINCODE.setPINCODE(bottomSheetPincode.getText().toString());
            currentLocation.setText(PINCODE.getPINCODE());
            editor.putString("PINCODE",PINCODE.getPINCODE());
            editor.commit();
            dialog.dismiss();
        });


        location.setOnClickListener(v -> dialog.show());
        fetchLocation();
        return view;
    }




    private void fetchLocation() {
        String pin = sharedPreferences.getString("PINCODE",null);
        if(pin == null) {

            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                GPSTracker gps = new GPSTracker(getContext());
                if(gps.canGetLocation()){
                    String location[] = getCompleteAddressString(gps.getLatitude(), gps.getLongitude());
                    PINCODE.setPINCODE(location[1]);
                    currentLocation.setText(PINCODE.getPINCODE());
                    editor.putString("PINCODE",PINCODE.getPINCODE());
                    editor.putString("ADDRESS",location[0]);
                    //Toast.makeText(getContext(), location[0], Toast.LENGTH_SHORT).show();
                    editor.commit();
                } else {
                    Toast.makeText(getContext(), "Enter Your Pincode Manually", Toast.LENGTH_SHORT).show();
                    dialog.show();
                }
            } else {

                Toast.makeText(getContext(), "Enter Your Pincode Manually", Toast.LENGTH_SHORT).show();
                dialog.show();
            }

           // dialog.show();
        } else {
            PINCODE.setPINCODE(pin);
            bottomSheetPincode.setText(PINCODE.getPINCODE());
            currentLocation.setText(PINCODE.getPINCODE());
        }
    }


    private String[] getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd[] = new String[2];
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);

                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                }
                strAdd[0] = strReturnedAddress.toString();
                strAdd[1] = returnedAddress.getPostalCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;


    private void enableLocation() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);



            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), REQUEST_LOCATION);

                                //finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }



    public static class Pincode {
        private String PINCODE;

        public void setPINCODE(String PINCODE) {
            this.PINCODE = PINCODE;
        }

        public String getPINCODE() {
            return PINCODE;
        }
    }

}