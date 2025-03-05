package com.canhub.canhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listGroupTitles;
    private HashMap<String, List<ItemHijo>> listChildData;

    public CustomExpandableListAdapter(Context context, List<String> listGroupTitles, HashMap<String, List<ItemHijo>> listChildData) {
        this.context = context;
        this.listGroupTitles = listGroupTitles;
        this.listChildData = listChildData;
    }

    @Override
    public int getGroupCount() {
        return listGroupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChildData.get(listGroupTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroupTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChildData.get(listGroupTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grupo_item, null);
        }

        TextView groupTitle = convertView.findViewById(R.id.group_title);
        ImageView arrowIcon = convertView.findViewById(R.id.flechaAbajo);

        groupTitle.setText((String) getGroup(groupPosition));

        // Cambia la rotación de la flecha dependiendo del estado del grupo
        if (isExpanded) {
            arrowIcon.setRotation(180);  // Flecha hacia arriba
        } else {
            arrowIcon.setRotation(0);    // Flecha hacia abajo
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_hijo, null);
        }

        ImageView childIcon = convertView.findViewById(R.id.child_icon);
        TextView childText = convertView.findViewById(R.id.child_text);

        ItemHijo childItem = (ItemHijo) getChild(groupPosition, childPosition);
        childIcon.setImageResource(childItem.getIconResId());
        childText.setText(childItem.getText());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
