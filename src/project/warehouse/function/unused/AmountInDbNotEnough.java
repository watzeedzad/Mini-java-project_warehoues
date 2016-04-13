package project.warehouse.function.unused;

//  @author jirawat
public class AmountInDbNotEnough extends Exception {
    public AmountInDbNotEnough() {
        super("Product amount not enough!");
    }
}