package project.warehouse.function.unused;

//  @author jirawat
public class AmountNotEnough extends Exception {
    public AmountNotEnough() {
        super("Product amount not enough!");
    }
}