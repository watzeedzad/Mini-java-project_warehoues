package project.warehouse.function;

//  @author jirawat
public class AlreadyUsedUsername extends Exception {
    public AlreadyUsedUsername() {
        super("Already used username!");
    }
}