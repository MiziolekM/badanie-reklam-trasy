package dev.mateusz.demo.view;

public enum FxmlView {

    HOME {
        @Override
		public String getTitle() {
            return "Badanie Reklam Trasy";
        }

        @Override
		public String getFxmlFile() {
            return "/fxml/home.fxml";
        }
    }, 
    ANALYSE {
        @Override
		public String getTitle() {
            return "Analiza wideo";
        }

        @Override
		public String getFxmlFile() {
            return "/fxml/analyse.fxml";
        }
    };
    
    public abstract String getTitle();
    public abstract String getFxmlFile();

}
