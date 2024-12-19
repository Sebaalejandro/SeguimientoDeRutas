package com.example.seguimientoderutas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private final Context context;
    private final List<RouteData> routeList;

    public RouteAdapter(Context context, List<RouteData> routeList) {
        this.context = context;
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        RouteData routeData = routeList.get(position);
        holder.tvRouteDetailName.setText("Nombre: " + routeData.getRouteName());
        holder.tvRouteDetailDistance.setText("Distancia: " + routeData.getTotalDistance() + " m");
        holder.tvRouteDetailDuration.setText("Duración: " + formatDuration(routeData.getEndTime() - routeData.getStartTime()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RouteDetailActivity.class);
            intent.putExtra("routeId", routeData.getRouteId());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView tvRouteDetailName, tvRouteDetailDistance, tvRouteDetailDuration;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteDetailName = itemView.findViewById(R.id.tvRouteDetailName);
            tvRouteDetailDistance = itemView.findViewById(R.id.tvRouteDetailDistance);
            tvRouteDetailDuration = itemView.findViewById(R.id.tvRouteDetailDuration);
        }
    }

    private String formatDuration(long durationMillis) {
        long seconds = durationMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }
}
