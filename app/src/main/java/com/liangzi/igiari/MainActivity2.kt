package com.liangzi.igiari

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.liangzi.igiari.databinding.ActivityMainBinding


class MainActivity2 : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var 前加速度: Double = 0.0
    private var 敏感度: Int = 0
    private var 选中视频: Int = R.raw.bigiari
    private var 视频链接 = Uri.parse("android.resource://" + getPackageName() + "/" + 选中视频)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 获取传感器管理器
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // 获取震动传感器
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        binding.seekbar.setOnSeekBarChangeListener(object :
            android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: android.widget.SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                敏感度 = progress
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                Toast.makeText(this@MainActivity2, "敏感度：$敏感度", Toast.LENGTH_SHORT).show()
            }
        })
        binding.apply {

            igiari.setOnClickListener {
                选中视频 = R.raw.bigiari
                视频链接 = Uri.parse("android.resource://" + getPackageName() + "/" + 选中视频)
                video.setVideoURI(视频链接)
            }
            matta.setOnClickListener {
                选中视频 = R.raw.bmatta
                视频链接 = Uri.parse("android.resource://" + getPackageName() + "/" + 选中视频)
                video.setVideoURI(视频链接)
            }
            chotto.setOnClickListener {
                选中视频 = R.raw.bchotto
                视频链接 = Uri.parse("android.resource://" + getPackageName() + "/" + 选中视频)
                video.setVideoURI(视频链接)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // 计算加速度的绝对值

            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble())

            binding.textView.text =
                "加速度：" + String.format("%.3f", acceleration) + "\n加速度变化：" +
                        String.format("%.3f", Math.abs(前加速度 - acceleration)) +
                        "\n↓敏感度$敏感度↓"

            // 判断加速度变化是否超过阈值 敏感度100，任何加速度变化都会触发
            if (Math.abs(前加速度 - acceleration) > 2.0 - 敏感度 * (2.0 / 100.0)) {
                binding.video.start()
            }
            前加速度 = acceleration
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onResume() {
        super.onResume()
        // 注册传感器监听器
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        // 取消注册传感器监听器
        sensorManager.unregisterListener(this)
    }
}