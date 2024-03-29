/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.heartrate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.antplus.pluginsampler.datatransfer.Logs;
import com.dsi.ant.antplus.pluginsampler.datatransfer.WheelPiesClient;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.ICalculatedRrIntervalReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.IHeartRateDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.IPage4AddtDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.RrFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IRssiReceiver;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.ICumulativeOperatingTimeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IManufacturerAndSerialReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IVersionAndModelReceiver;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Base class to connects to Heart Rate Plugin and display all the event data.
 */
public abstract class Activity_HeartRateDisplayBase extends Activity
{
    protected abstract void requestAccessToPcc();
    
    AntPlusHeartRatePcc hrPcc = null;
    protected PccReleaseHandle<AntPlusHeartRatePcc> releaseHandle = null;
    
    public TextView tv_status;
    
    public TextView tv_estTimestamp;
    
    public TextView tv_rssi;
    
    public TextView tv_computedHeartRate;
    public TextView tv_heartBeatCounter;
    public TextView tv_heartBeatEventTime;
    
    public TextView tv_manufacturerSpecificByte;
    public TextView tv_previousHeartBeatEventTime;
    
    public TextView tv_calculatedRrInterval;
    
    public TextView tv_cumulativeOperatingTime;
    
    public TextView tv_manufacturerID;
    public TextView tv_serialNumber;
    
    public TextView tv_hardwareVersion;
    public TextView tv_softwareVersion;
    public TextView tv_modelNumber;
    
    public TextView tv_dataStatus;
    public TextView tv_rrFlag;
    Button btn_Active;
    String mstrUUID;
    int mnState;
    WheelPiesClient wheelPiesClient = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        wheelPiesClient = new WheelPiesClient();
        handleReset();
        mnState = 0;
    }
    
    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */
    protected void handleReset()
    {
        //Release the old access if it exists
        if (releaseHandle != null)
        {
            releaseHandle.close();
        }
        
        requestAccessToPcc();
    }
    
    protected void showDataDisplay(String status)
    {
        setContentView(R.layout.activity_heart_rate);
        
        tv_status = (TextView) findViewById(R.id.textView_Status);
        
        tv_estTimestamp = (TextView) findViewById(R.id.textView_EstTimestamp);
        
        tv_rssi = (TextView) findViewById(R.id.textView_Rssi);
        
        tv_computedHeartRate = (TextView) findViewById(R.id.textView_ComputedHeartRate);
        tv_heartBeatCounter = (TextView) findViewById(R.id.textView_HeartBeatCounter);
        tv_heartBeatEventTime = (TextView) findViewById(R.id.textView_HeartBeatEventTime);
        
        tv_manufacturerSpecificByte = (TextView) findViewById(R.id
                .textView_ManufacturerSpecificByte);
        tv_previousHeartBeatEventTime = (TextView) findViewById(R.id
                .textView_PreviousHeartBeatEventTime);
        
        tv_calculatedRrInterval = (TextView) findViewById(R.id.textView_CalculatedRrInterval);
        
        tv_cumulativeOperatingTime = (TextView) findViewById(R.id.textView_CumulativeOperatingTime);
        
        tv_manufacturerID = (TextView) findViewById(R.id.textView_ManufacturerID);
        tv_serialNumber = (TextView) findViewById(R.id.textView_SerialNumber);
        
        tv_hardwareVersion = (TextView) findViewById(R.id.textView_HardwareVersion);
        tv_softwareVersion = (TextView) findViewById(R.id.textView_SoftwareVersion);
        tv_modelNumber = (TextView) findViewById(R.id.textView_ModelNumber);
        
        tv_dataStatus = (TextView) findViewById(R.id.textView_DataStatus);
        tv_rrFlag = (TextView) findViewById(R.id.textView_rRFlag);
        
        btn_Active = (Button) findViewById(R.id.button_active);
        btn_Active.setTag(0);
        btn_Active.setText("運動已結束");
        //Reset the text display
        tv_status.setText(status);
        
        tv_estTimestamp.setText("---");
        
        tv_rssi.setText("---");
        
        tv_computedHeartRate.setText("---");
        tv_heartBeatCounter.setText("---");
        tv_heartBeatEventTime.setText("---");
        
        tv_manufacturerSpecificByte.setText("---");
        tv_previousHeartBeatEventTime.setText("---");
        
        tv_calculatedRrInterval.setText("---");
        
        tv_cumulativeOperatingTime.setText("---");
        
        tv_manufacturerID.setText("---");
        tv_serialNumber.setText("---");
        
        tv_hardwareVersion.setText("---");
        tv_softwareVersion.setText("---");
        tv_modelNumber.setText("---");
        tv_dataStatus.setText("---");
        tv_rrFlag.setText("---");
        mstrUUID = "";
        
        btn_Active.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int nRun = (int) v.getTag();
                if (nRun == 1)
                {
                    mnState = 2;
                    //mstrUUID = "";
                    v.setTag(0);
                    btn_Active.setText("運動已結束");
                }
                else
                {
                    mnState = 1;
                    UUID uuid = UUID.randomUUID();
                    mstrUUID = uuid.toString();
                    v.setTag(1);
                    btn_Active.setText("運動已開始");
                }
            }
        });
    }
    
    /**
     * Switches the active view to the data display and subscribes to all the data events
     */
    public void subscribeToHrEvents()
    {
        wheelPiesClient.start(null);
        hrPcc.subscribeHeartRateDataEvent(new IHeartRateDataReceiver()
        {
            @Override
            public void onNewHeartRateData(final long estTimestamp, EnumSet<EventFlag>
                    eventFlags, final int computedHeartRate, final long heartBeatCount, final
            BigDecimal heartBeatEventTime, final DataState dataState)
            {
                // Mark heart rate with asterisk if zero detected
                final String textHeartRate = String.valueOf(computedHeartRate) + ((DataState
                        .ZERO_DETECTED.equals(dataState)) ? "*" : "");
                
                // Mark heart beat count and heart beat event time with asterisk if initial value
                final String textHeartBeatCount = String.valueOf(heartBeatCount) + ((DataState
                        .INITIAL_VALUE.equals(dataState)) ? "*" : "");
                final String textHeartBeatEventTime = String.valueOf(heartBeatEventTime) + (
                        (DataState.INITIAL_VALUE.equals(dataState)) ? "*" : "");
                
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        
                        tv_computedHeartRate.setText(textHeartRate);
                        tv_heartBeatCounter.setText(textHeartBeatCount);
                        tv_heartBeatEventTime.setText(textHeartBeatEventTime);
                        
                        tv_dataStatus.setText(dataState.toString());
                        
                        sendData();
                    }
                });
            }
        });
        
        hrPcc.subscribePage4AddtDataEvent(new IPage4AddtDataReceiver()
        {
            @Override
            public void onNewPage4AddtData(final long estTimestamp, final EnumSet<EventFlag>
                    eventFlags, final int manufacturerSpecificByte, final BigDecimal
                    previousHeartBeatEventTime)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        
                        tv_manufacturerSpecificByte.setText(String.format("0x%02X",
                                manufacturerSpecificByte));
                        tv_previousHeartBeatEventTime.setText(String.valueOf
                                (previousHeartBeatEventTime));
                    }
                });
            }
        });
        
        hrPcc.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver()
        {
            @Override
            public void onNewCumulativeOperatingTime(final long estTimestamp, final
            EnumSet<EventFlag> eventFlags, final long cumulativeOperatingTime)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        
                        tv_cumulativeOperatingTime.setText(String.valueOf(cumulativeOperatingTime));
                    }
                });
            }
        });
        
        hrPcc.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver()
        {
            @Override
            public void onNewManufacturerAndSerial(final long estTimestamp, final
            EnumSet<EventFlag> eventFlags, final int manufacturerID, final int serialNumber)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        
                        tv_manufacturerID.setText(String.valueOf(manufacturerID));
                        tv_serialNumber.setText(String.valueOf(serialNumber));
                    }
                });
            }
        });
        
        hrPcc.subscribeVersionAndModelEvent(new IVersionAndModelReceiver()
        {
            @Override
            public void onNewVersionAndModel(final long estTimestamp, final EnumSet<EventFlag>
                    eventFlags, final int hardwareVersion, final int softwareVersion, final int
                    modelNumber)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        
                        tv_hardwareVersion.setText(String.valueOf(hardwareVersion));
                        tv_softwareVersion.setText(String.valueOf(softwareVersion));
                        tv_modelNumber.setText(String.valueOf(modelNumber));
                    }
                });
            }
        });
        
        hrPcc.subscribeCalculatedRrIntervalEvent(new ICalculatedRrIntervalReceiver()
        {
            @Override
            public void onNewCalculatedRrInterval(final long estTimestamp, EnumSet<EventFlag>
                    eventFlags, final BigDecimal rrInterval, final RrFlag flag)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        tv_rrFlag.setText(flag.toString());
                        
                        // Mark RR with asterisk if source is not cached or page 4
                        if (flag.equals(RrFlag.DATA_SOURCE_CACHED) || flag.equals(RrFlag
                                .DATA_SOURCE_PAGE_4))
                        
                        {
                            tv_calculatedRrInterval.setText(String.valueOf(rrInterval));
                        }
                        else
                        {
                            tv_calculatedRrInterval.setText(String.valueOf(rrInterval) + "*");
                        }
                    }
                });
            }
        });
        
        hrPcc.subscribeRssiEvent(new IRssiReceiver()
        {
            @Override
            public void onRssiData(final long estTimestamp, final EnumSet<EventFlag> evtFlags,
                    final int rssi)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        tv_rssi.setText(String.valueOf(rssi) + " dBm");
                    }
                });
            }
        });
    }
    
    private void sendData()
    {
        JSONObject jsonObject = new JSONObject();
        
        try
        {
            jsonObject.put("activeId", mstrUUID);
            jsonObject.put("state", mnState);
            if(2 == mnState) // 運動結束
            {
                mstrUUID = "";
            }
            if (0 != mnState)
            {
                mnState = 0;
            }
            jsonObject.put("estTimestamp", tv_estTimestamp.getText().toString());
            jsonObject.put("computedHeartRate", tv_computedHeartRate.getText().toString());
            jsonObject.put("heartBeatCounter", tv_heartBeatCounter.getText().toString());
            jsonObject.put("heartBeatEventTime", tv_heartBeatEventTime.getText().toString());
            jsonObject.put("dataStatus", tv_dataStatus.getText().toString());
            jsonObject.put("manufacturerSpecificByte", tv_manufacturerSpecificByte.getText()
                    .toString());
            jsonObject.put("previousHeartBeatEventTime", tv_previousHeartBeatEventTime.getText()
                    .toString());
            jsonObject.put("cumulativeOperatingTime", tv_cumulativeOperatingTime.getText()
                    .toString());
            jsonObject.put("manufacturerID", tv_manufacturerID.getText().toString());
            jsonObject.put("serialNumber", tv_serialNumber.getText().toString());
            jsonObject.put("hardwareVersion", tv_hardwareVersion.getText().toString());
            jsonObject.put("softwareVersion", tv_softwareVersion.getText().toString());
            jsonObject.put("modelNumber", tv_modelNumber.getText().toString());
            jsonObject.put("rrFlag", tv_rrFlag.getText().toString());
            jsonObject.put("calculatedRrInterval", tv_calculatedRrInterval.getText().toString());
            jsonObject.put("rssi", tv_rssi.getText().toString());
            wheelPiesClient.send(jsonObject);
        }
        catch (Exception e)
        {
            Logs.showError("JSONObject Exception: " + e.toString());
        }
        
    }
    
    protected IPluginAccessResultReceiver<AntPlusHeartRatePcc> base_IPluginAccessResultReceiver =
            new IPluginAccessResultReceiver<AntPlusHeartRatePcc>()
    {
        //Handle the result, connecting to events on success or reporting failure to user.
        @Override
        public void onResultReceived(AntPlusHeartRatePcc result, RequestAccessResult resultCode,
                DeviceState initialDeviceState)
        {
            showDataDisplay("Connecting...");
            switch (resultCode)
            {
                case SUCCESS:
                    hrPcc = result;
                    tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    subscribeToHrEvents();
                    if (!result.supportsRssi())
                    {
                        tv_rssi.setText("N/A");
                    }
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "Channel Not Available",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "ANT Adapter Not " +
                            "Available" + ". Built-in ANT hardware or external adapter required"
                            + ".", Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    //Note: Since we compose all the params ourself, we should never see this result
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "Bad request parameters.",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "RequestAccess failed. " +
                            "See" + " logcat for details.", Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder
                            (Activity_HeartRateDisplayBase.this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr.setMessage("The required service\n\"" + AntPlusHeartRatePcc
                            .getMissingDependencyName() + "\"\n was not found. You need to " +
                            "install the ANT+ Plugins service or you may need to update your " +
                            "existing version if you already have it. Do you want to launch the "
                            + "Play Store to get it?");
                    adlgBldr.setCancelable(true);
                    adlgBldr.setPositiveButton("Go to Store", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent startStore = null;
                            startStore = new Intent(Intent.ACTION_VIEW, Uri.parse
                                    ("market://details?id=" + AntPlusHeartRatePcc
                                            .getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            
                            Activity_HeartRateDisplayBase.this.startActivity(startStore);
                        }
                    });
                    adlgBldr.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    
                    final AlertDialog waitDialog = adlgBldr.create();
                    waitDialog.show();
                    break;
                case USER_CANCELLED:
                    tv_status.setText("Cancelled. Do Menu->Reset.");
                    break;
                case UNRECOGNIZED:
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "Failed: UNRECOGNIZED. " +
                            "PluginLib Upgrade Required?", Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "Unrecognized result: " +
                            resultCode, Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
            }
        }
    };
    
    //Receives state changes and shows it on the status display line
    protected IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver = new
            IDeviceStateChangeReceiver()
    {
        @Override
        public void onDeviceStateChange(final DeviceState newDeviceState)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    tv_status.setText(hrPcc.getDeviceName() + ": " + newDeviceState);
                }
            });
            
            
        }
    };
    
    @Override
    protected void onDestroy()
    {
        if (releaseHandle != null)
        {
            releaseHandle.close();
        }
        super.onDestroy();
        wheelPiesClient.stop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_heart_rate, menu);
        return true;
    }
    
    
}
