package com.pedrodavidmcr.agarden.plants.view.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.pedrodavidmcr.agarden.R
import com.pedrodavidmcr.agarden.base.view.DataView
import com.pedrodavidmcr.agarden.base.view.animation.setOnlyEndAnimation
import com.pedrodavidmcr.agarden.base.view.color
import com.pedrodavidmcr.agarden.irrigation.view.activity.PlantConfigurationActivity
import com.pedrodavidmcr.agarden.plants.domain.Plant
import com.pedrodavidmcr.agarden.plants.domain.Sample
import com.pedrodavidmcr.agarden.plants.presenter.PlantDetailPresenter
import com.pedrodavidmcr.agarden.plants.view.animation.ProgressAnimation
import com.pedrodavidmcr.agarden.plants.view.getSharedPlant
import com.pedrodavidmcr.agarden.plants.view.toBundle
import kotlinx.android.synthetic.main.activity_plants_details.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick


class PlantsDetailsActivity : AppCompatActivity(), DataView<Sample> {
  private val presenter: PlantDetailPresenter by lazy { PlantDetailPresenter(this) }
  private val plant: Plant by lazy { intent.getSharedPlant() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_plants_details)
    animateActivityEntry()
    image.onClick {
      startActivity(
          intentFor<PlantConfigurationActivity>().putExtras(plant.toBundle())
      )
    }
    formatCharts(temperatureStat, humidityStat)
    presenter.getSamplesFrom(plant)
  }

  private fun formatCharts(vararg charts: LineChart) {
    charts.forEach {
      it.setDrawBorders(false)
      it.setBorderWidth(0F)
      it.axisRight.setDrawLabels(false)
      it.xAxis.setDrawLabels(false)
      it.setBackgroundColor(color(android.R.color.white))
      it.legend.setCustom(emptyArray())
    }
  }

  override fun onDataLoaded(data: List<Sample>) {
    val temperatures = data
        .mapIndexed { index, sample -> Entry(index.toFloat(), sample.temperature.toFloat()) }
    val humidity = data
        .mapIndexed { index, sample -> Entry(index.toFloat(), sample.humidity.toFloat()) }

    val tempDataSet = LineDataSet(temperatures, "temperatures").apply {
      lineWidth = 6F
      color = color(android.R.color.holo_green_light)
    }
    val humDataSet = LineDataSet(humidity, "humidity").apply {
      lineWidth = 6F
      color = color(android.R.color.holo_blue_light)
    }

    configureTemperatureChart(tempDataSet)
    configureHumidityChart(humDataSet)
  }

  private fun configureTemperatureChart(tempDataSet: LineDataSet) = with(temperatureStat) {
    data = LineData(tempDataSet)
    description = Description().also { it.text = "Last temperature measures" }
    setVisibleYRange(0F, 50F, YAxis.AxisDependency.LEFT)
    fitScreen()
    invalidate()
  }

  private fun configureHumidityChart(humDataSet: LineDataSet) = with(humidityStat) {
    data = LineData(humDataSet)
    description = Description().also { it.text = "Last humidity measures" }
    setVisibleYRange(0F, 100F, YAxis.AxisDependency.LEFT)
    fitScreen()
    invalidate()
  }


  private fun animateActivityEntry() {
    supportPostponeEnterTransition()
    window.sharedElementEnterTransition.setOnlyEndAnimation {

      val height = image.height.toFloat()
      val width = image.width.toFloat()
      val pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, 0.5F)
      val pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F, 0.5F)
      val scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(image, pvhX, pvhY)
      val setAnimation = AnimatorSet()
      setAnimation.play(scaleAnimation)
      bar.visibility = VISIBLE
      bar2.visibility = VISIBLE
      bar3.visibility = VISIBLE
      setAnimation.apply {
        setOnlyEndAnimation {
          bar.startAnimation(ProgressAnimation(bar, 0F, 90F).apply { duration = 1000 })
          bar2.startAnimation(ProgressAnimation(bar2, 0F, 67F).apply { duration = 500 })
          bar3.startAnimation(ProgressAnimation(bar3, 0F, 70F).apply { duration = 700 })
        }
        start()
      }
    }
    plantName.text = plant.name
    ViewCompat.setTransitionName(maincard, intent.extras.getString("transitionRoot"))
    ViewCompat.setTransitionName(image, intent.extras.getString("transitionImage"))
    ViewCompat.setTransitionName(plantName, intent.extras.getString("transitionName"))
    supportStartPostponedEnterTransition()
  }

  override fun onBackPressed() {

    bar.startAnimation(ProgressAnimation(bar, bar.progress.toFloat(), 0F).apply {
      duration = 1000
      setOnlyEndAnimation {
        val pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5F, 1F)
        val pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5F, 1F)
        val scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(image, pvhX, pvhY)
        val setAnimation = AnimatorSet()
        setAnimation.apply {
          play(scaleAnimation)
          setOnlyEndAnimation {
            bar.visibility = INVISIBLE
            bar2.visibility = INVISIBLE
            bar3.visibility = INVISIBLE
            Handler().postDelayed({
              super.onBackPressed()
            }, 300)
          }
          start()
        }

      }
    })
    bar2.startAnimation(ProgressAnimation(bar2, bar2.progress.toFloat(), 0F).apply { duration = 500 })
    bar3.startAnimation(ProgressAnimation(bar3, bar3.progress.toFloat(), 0F).apply { duration = 700 })
  }
}
