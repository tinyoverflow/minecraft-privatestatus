package me.tinyoverflow.privatestatus;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

import java.util.concurrent.Callable;

public class PrivateStatusMetrics implements Callable<String>
{
    private Integer expirationMinutesMetric = 0;

    public PrivateStatusMetrics(PrivateStatus plugin, int pluginId)
    {
        Metrics metrics = new Metrics(plugin, pluginId);
        metrics.addCustomChart(new SimplePie("expiration_minutes", this));
    }

    @Override
    public String call()
    {
        return expirationMinutesMetric.toString();
    }

    public void setExpirationMinutesMetric(int expirationMinutesMetric)
    {
        this.expirationMinutesMetric = expirationMinutesMetric;
    }
}
