package org.iii.wheelpiedemo.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.datatransfer.Logs;
import com.dsi.ant.antplus.pluginsampler.multidevicesearch.Activity_MultiDeviceSearchSampler;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import org.iii.wheelpiedemo.R;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.EnumSet;


public class TrainingSampleActivity extends Activity
{

    /**
     * ANT+ Library
     */
    private AntPlusHeartRatePcc hrPcc = null;
    protected PccReleaseHandle<AntPlusHeartRatePcc> releaseHandle = null;


    /**
     * Layout
     */
    private LinearLayout btnScanDevice;
    private TextView textView_ComputedHeartRate = null;
    private TextView textView9 = null;
    private TextView textView10 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_main);
        textView_ComputedHeartRate = findViewById(R.id.textView_ComputedHeartRate);
        textView9 = findViewById(R.id.textView9);
        textView10 = findViewById(R.id.textView10);
        findViewById(R.id.startbutton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestAccessToPcc();
            }
        });


    }


    private void requestAccessToPcc()
    {
        Intent intent = getIntent();
        if (intent.hasExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT))
        {
            // device has already been selected through the multi-device search
            MultiDeviceSearch.MultiDeviceSearchResult result = intent
                    .getParcelableExtra(Activity_MultiDeviceSearchSampler
                                                .EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
            releaseHandle = AntPlusHeartRatePcc.requestAccess(this, result.getAntDeviceNumber(), 0,
                                                              base_IPluginAccessResultReceiver,
                                                              base_IDeviceStateChangeReceiver);
        }
        else
        {
            // starts the plugins UI search
            releaseHandle = AntPlusHeartRatePcc.requestAccess(this, this,
                                                              base_IPluginAccessResultReceiver,
                                                              base_IDeviceStateChangeReceiver);
        }
    }


    protected AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc>
            base_IPluginAccessResultReceiver =
            new AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc>()
            {
                //Handle the result, connecting to events on success or reporting failure to user.
                @Override
                public void onResultReceived(
                        AntPlusHeartRatePcc result, RequestAccessResult resultCode,
                        DeviceState initialDeviceState
                                            )
                {
                    switch (resultCode)
                    {
                        case SUCCESS:
                            hrPcc = result;
//                            tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                            subscribeToHrEvents();
//                            if (!result.supportsRssi())
//                            {
//                                tv_rssi.setText("N/A");
//                            }
                            break;
                        case CHANNEL_NOT_AVAILABLE:
                            Toast.makeText(TrainingSampleActivity.this, "Channel Not Available",
                                           Toast.LENGTH_SHORT).show();
                            // tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        case ADAPTER_NOT_DETECTED:
                            Toast.makeText(TrainingSampleActivity.this, "ANT Adapter Not " +
                                    "Available" + ". Built-in ANT hardware or external adapter " +
                                    "required"
                                    + ".", Toast.LENGTH_SHORT).show();
                            // tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        case BAD_PARAMS:
                            //Note: Since we compose all the params ourself, we should never see
                            // this result
                            Toast.makeText(TrainingSampleActivity.this, "Bad request parameters.",
                                           Toast.LENGTH_SHORT).show();
                            // tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        case OTHER_FAILURE:
                            Toast.makeText(TrainingSampleActivity.this, "RequestAccess failed. " +
                                    "See" + " logcat for details.", Toast.LENGTH_SHORT).show();
                            //      tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        case DEPENDENCY_NOT_INSTALLED:
                            //    tv_status.setText("Error. Do Menu->Reset.");
                            AlertDialog.Builder adlgBldr = new AlertDialog.Builder
                                    (TrainingSampleActivity.this);
                            adlgBldr.setTitle("Missing Dependency");
                            adlgBldr.setMessage("The required service\n\"" + AntPlusHeartRatePcc
                                    .getMissingDependencyName() + "\"\n was not found. You need " +
                                                        "to " +
                                                        "install the ANT+ Plugins service or you " +
                                                        "may need to update your " +
                                                        "existing version if you already have it." +
                                                        " Do you want to launch the "
                                                        + "Play Store to get it?");
                            adlgBldr.setCancelable(true);
                            adlgBldr.setPositiveButton("Go to Store", new DialogInterface
                                    .OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent startStore = null;
                                    startStore = new Intent(Intent.ACTION_VIEW, Uri.parse
                                            ("market://details?id=" + AntPlusHeartRatePcc
                                                    .getMissingDependencyPackageName()));
                                    startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    TrainingSampleActivity.this.startActivity(startStore);
                                }
                            });
                            adlgBldr.setNegativeButton("Cancel", new DialogInterface
                                    .OnClickListener()
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
                            //     tv_status.setText("Cancelled. Do Menu->Reset.");
                            break;
                        case UNRECOGNIZED:
                            Toast.makeText(TrainingSampleActivity.this, "Failed: UNRECOGNIZED. " +
                                    "PluginLib Upgrade Required?", Toast.LENGTH_SHORT).show();
                            //       tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        default:
                            Toast.makeText(TrainingSampleActivity.this, "Unrecognized result: " +
                                    resultCode, Toast.LENGTH_SHORT).show();
                            //    tv_status.setText("Error. Do Menu->Reset.");
                            break;
                    }
                }
            };

    //Receives state changes and shows it on the status display line
    protected AntPluginPcc.IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver = new
            AntPluginPcc.IDeviceStateChangeReceiver()
            {
                @Override
                public void onDeviceStateChange(final DeviceState newDeviceState)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //          tv_status.setText(hrPcc.getDeviceName() + ": " +
                            // newDeviceState);
                        }
                    });


                }
            };


    public void subscribeToHrEvents()
    {
        hrPcc.subscribeHeartRateDataEvent(new AntPlusHeartRatePcc.IHeartRateDataReceiver()
        {
            @Override
            public void onNewHeartRateData(
                    final long estTimestamp, EnumSet<EventFlag>
                    eventFlags, final int computedHeartRate, final long heartBeatCount, final
                    BigDecimal heartBeatEventTime, final AntPlusHeartRatePcc.DataState dataState
                                          )
            {
                // Mark heart rate with asterisk if zero detected
                final String textHeartRate = String.valueOf(computedHeartRate) + (
                        (AntPlusHeartRatePcc.DataState
                                .ZERO_DETECTED.equals(dataState)) ? "*" : "");

                // Mark heart beat count and heart beat event time with asterisk if initial value
                final String textHeartBeatCount = String.valueOf(heartBeatCount) + (
                        (AntPlusHeartRatePcc.DataState
                                .INITIAL_VALUE.equals(dataState)) ? "*" : "");
                final String textHeartBeatEventTime = String.valueOf(heartBeatEventTime) + (
                        (AntPlusHeartRatePcc.DataState.INITIAL_VALUE.equals(dataState)) ? "*" : "");

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        textView_ComputedHeartRate.setText(textHeartRate);
                        //  tv_heartBeatCounter.setText(textHeartBeatCount);
                        //  tv_heartBeatEventTime.setText(textHeartBeatEventTime);

                        //  tv_dataStatus.setText(dataState.toString());

                        //   sendData();
                    }
                });
            }
        });

        hrPcc.subscribePage4AddtDataEvent(new AntPlusHeartRatePcc.IPage4AddtDataReceiver()
        {
            @Override
            public void onNewPage4AddtData(
                    final long estTimestamp, final EnumSet<EventFlag>
                    eventFlags, final int manufacturerSpecificByte, final BigDecimal
                            previousHeartBeatEventTime
                                          )
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //       tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        //       tv_manufacturerSpecificByte.setText(String.format("0x%02X",
                        //manufacturerSpecificByte));
                        //       tv_previousHeartBeatEventTime.setText(String.valueOf
                        //(previousHeartBeatEventTime));
                    }
                });
            }
        });

        hrPcc.subscribeCumulativeOperatingTimeEvent(new AntPlusLegacyCommonPcc
                .ICumulativeOperatingTimeReceiver()
        {
            @Override
            public void onNewCumulativeOperatingTime(
                    final long estTimestamp, final
            EnumSet<EventFlag> eventFlags, final long cumulativeOperatingTime
                                                    )
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //        tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        //        tv_cumulativeOperatingTime.setText(String.valueOf
                        // (cumulativeOperatingTime));
                    }
                });
            }
        });

        hrPcc.subscribeManufacturerAndSerialEvent(new AntPlusLegacyCommonPcc
                .IManufacturerAndSerialReceiver()
        {
            @Override
            public void onNewManufacturerAndSerial(
                    final long estTimestamp, final
            EnumSet<EventFlag> eventFlags, final int manufacturerID, final int serialNumber
                                                  )
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //        tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        //        tv_manufacturerID.setText(String.valueOf(manufacturerID));
                        //        tv_serialNumber.setText(String.valueOf(serialNumber));
                    }
                });
            }
        });

        hrPcc.subscribeVersionAndModelEvent(new AntPlusLegacyCommonPcc.IVersionAndModelReceiver()
        {
            @Override
            public void onNewVersionAndModel(
                    final long estTimestamp, final EnumSet<EventFlag>
                    eventFlags, final int hardwareVersion, final int softwareVersion, final int
                            modelNumber
                                            )
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //       tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        //       tv_hardwareVersion.setText(String.valueOf(hardwareVersion));
                        //       tv_softwareVersion.setText(String.valueOf(softwareVersion));
                        //       tv_modelNumber.setText(String.valueOf(modelNumber));
                    }
                });
            }
        });

        hrPcc.subscribeCalculatedRrIntervalEvent(new AntPlusHeartRatePcc
                .ICalculatedRrIntervalReceiver()
        {
            @Override
            public void onNewCalculatedRrInterval(
                    final long estTimestamp, EnumSet<EventFlag>
                    eventFlags, final BigDecimal rrInterval, final AntPlusHeartRatePcc.RrFlag flag
                                                 )
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //       tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        //       tv_rrFlag.setText(flag.toString());

                        // Mark RR with asterisk if source is not cached or page 4
                        if (flag.equals(AntPlusHeartRatePcc.RrFlag.DATA_SOURCE_CACHED) || flag
                                .equals(AntPlusHeartRatePcc.RrFlag
                                                .DATA_SOURCE_PAGE_4))

                        {
                            //           tv_calculatedRrInterval.setText(String.valueOf
                            // (rrInterval));
                        }
                        else
                        {
                            //           tv_calculatedRrInterval.setText(String.valueOf
                            // (rrInterval) + "*");
                        }
                    }
                });
            }
        });

        hrPcc.subscribeRssiEvent(new AntPlusCommonPcc.IRssiReceiver()
        {
            @Override
            public void onRssiData(
                    final long estTimestamp, final EnumSet<EventFlag> evtFlags,
                    final int rssi
                                  )
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //       tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        //       tv_rssi.setText(String.valueOf(rssi) + " dBm");
                    }
                });
            }
        });
    }


}
