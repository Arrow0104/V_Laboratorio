package Presentation.Models;

import Domain.Dtos.maintenances.MaintenanceResponseDto;
import Presentation.IObserver;
import Utilities.EventType;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MaintenancesTableModel extends AbstractTableModel implements IObserver {

    private final List<MaintenanceResponseDto> maintenances = new ArrayList<>();
    private final String[] columnNames = {"MaintenanceID","CarID", "Description", "Type", "Date"};

    @Override
    public int getRowCount() {
        return maintenances.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MaintenanceResponseDto m = maintenances.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> m.getId();           // MaintenanceID
            case 1 -> m.getCarId();        // CarID
            case 2 -> m.getDescription();  // Description
            case 3 -> m.getType();         // Type
            case 4 -> m.getDate();         // Date
            default -> null;
        };
    }
    @Override
    public void update(EventType eventType, Object data) {
        if (data == null) return;

        switch (eventType) {
            case CREATED -> {
                MaintenanceResponseDto newMaintenance = (MaintenanceResponseDto) data;
                maintenances.add(newMaintenance);
                fireTableRowsInserted(maintenances.size() - 1, maintenances.size() - 1);
            }
            case UPDATED -> {
                MaintenanceResponseDto updated = (MaintenanceResponseDto) data;
                for (int i = 0; i < maintenances.size(); i++) {
                    if (maintenances.get(i).getId().equals(updated.getId())) {
                        maintenances.set(i, updated);
                        fireTableRowsUpdated(i, i);
                        break;
                    }
                }
            }
            case DELETED -> {
                Long deletedId = (Long) data;
                for (int i = 0; i < maintenances.size(); i++) {
                    if (maintenances.get(i).getId().equals(deletedId)) {
                        maintenances.remove(i);
                        fireTableRowsDeleted(i, i);
                        break;
                    }
                }
            }
        }
    }

    public List<MaintenanceResponseDto> getMaintenances() {
        return new ArrayList<>(maintenances);
    }

    public void setMaintenances(List<MaintenanceResponseDto> newMaintenances) {
        maintenances.clear();
        if (newMaintenances != null) maintenances.addAll(newMaintenances);
        fireTableDataChanged();
    }
}
