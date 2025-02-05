package com.canhub.canhub;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.canhub.canhub.ItemHijo;
import com.canhub.canhub.R;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listGroupTitles; //creo una lista con los titulos de los grupos
    private HashMap<String, List<ItemHijo>> listChildData; //

    public CustomExpandableListAdapter(Context context, List<String> listGroupTitles, HashMap<String, List<ItemHijo>> listChildData) {
        this.context = context;
        this.listGroupTitles = listGroupTitles;
        this.listChildData = listChildData;
    }

    @Override
    public int getGroupCount() { //devuelve el numero de grupos
        return listGroupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {//devuelve el numero de hijos en un grupo especifico
        return listChildData.get(listGroupTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) { //obtiene el nombre de un grupo especifico
        return listGroupTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) { //obtiene el nombre de un hijo especifico
        return listChildData.get(listGroupTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) { //devuelve el id del grupo
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) { //devuelve el id del hijo dentro de ese grupo
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
        groupTitle.setText((String) getGroup(groupPosition));
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {//indica si un hijo es seleccionable
        return true;
    }
}
