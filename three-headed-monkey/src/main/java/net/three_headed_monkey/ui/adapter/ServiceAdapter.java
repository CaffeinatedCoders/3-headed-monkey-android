package net.three_headed_monkey.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import net.three_headed_monkey.R;
import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.data.ServiceSettings;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ServiceInfo serviceInfo;
        public TextView text_baseurl;
        public ImageButton btn_delete;
        private Context context;

        public ViewHolder(View layout, Context context) {
            super(layout);
            this.context = context;
            this.text_baseurl = (TextView) layout.findViewById(R.id.text_baseurl);
            this.btn_delete = (ImageButton) layout.findViewById(R.id.btn_delete);
        }

        public void setViews(ServiceInfo serviceInfo) {
            this.serviceInfo = serviceInfo;
            text_baseurl.setText(serviceInfo.baseUrl);
        }

    }

    private ServiceSettings serviceSettings;
    private Context context;

    public ServiceAdapter(ServiceSettings serviceSettings, Context context) {
        super();
        this.serviceSettings = serviceSettings;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_info_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.setViews(serviceSettings.get(position));
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.deleteServiceDialogTitle)
                        .setMessage(R.string.deleteServiceDialogMessage)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                serviceSettings.remove(serviceSettings.get(position));
                                notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return serviceSettings.size();
    }


}
