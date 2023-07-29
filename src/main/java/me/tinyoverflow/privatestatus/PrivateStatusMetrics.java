package me.tinyoverflow.privatestatus;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

import java.util.concurrent.Callable;

public class PrivateStatusMetrics implements Callable<String>
{
    private Integer expirationDaysMetric = 0;

    public PrivateStatusMetrics(PrivateStatus plugin, int pluginId)
    {
        Metrics metrics = new Metrics(plugin, pluginId);
        metrics.addCustomChart(new SimplePie("expiration_days", this));
    }

    @Override
    public String call()
    {
        return expirationDaysMetric.toString();
    }

    public void setExpirationDaysMetric(int expirationDaysMetric)
    {
        this.expirationDaysMetric = expirationDaysMetric;
    }
}
