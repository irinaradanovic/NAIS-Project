package rs.ac.uns.acs.nais.RestaurantTimeSeriesService.model;

public enum MenuEventType {
    INITIAL_CREATION,      // when menu is created, version=1
    MAJOR_INFO_CHANGE,     // major changes to menu (name, dates, timestamps)
    CATEGORY_ADDED,        // adding a new category to menu
    CATEGORY_REMOVED,      // removing a category from menu
    ITEM_PRICE_CHANGED     // changed price to an item creates a new version
}
