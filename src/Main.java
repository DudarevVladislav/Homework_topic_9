import java.util.*;

abstract class Instrument {
    public enum Type {
        STOCK, BOND, FUND
    }

    private final String ticker;
    private final int price;

    protected Instrument(String ticker, int price) {
        this.ticker = ticker;
        this.price = price;
    }

    public String getTicker() {
        return ticker;
    }

    public int getPrice() {
        return price;
    }

    public abstract Type getType();
}

class Stock extends Instrument {
    private final int dividends;

    public Stock(String ticker, int price, int dividends) {
        super(ticker, price);
        this.dividends = dividends;
    }

    public int getDividends() {
        return dividends;
    }

    @Override
    public Type getType() {
        return Type.STOCK;
    }
}

class Bond extends Instrument {
    private final double couponRate;

    public Bond(String ticker, int price, double couponRate) {
        super(ticker, price);
        this.couponRate = couponRate;
    }

    public double getCouponRate() {
        return couponRate;
    }

    @Override
    public Type getType() {
        return Type.BOND;
    }
}

class Fund extends Instrument {
    private final List<String> composition;

    public Fund(String ticker, int price, List<String> composition) {
        super(ticker, price);
        this.composition = new ArrayList<>(composition);
    }

    public List<String> getComposition() {
        return new ArrayList<>(composition);
    }

    @Override
    public Type getType() {
        return Type.FUND;
    }
}

class Position {
    private final Instrument instrument;
    private int quantity;

    public Position(Instrument instrument, int quantity) {
        this.instrument = instrument;
        this.quantity = quantity;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTicker() {
        return instrument.getTicker();
    }

    public void addQuantity(int amount) {
        quantity += amount;
    }

    public int getTotalPrice() {
        return instrument.getPrice() * quantity;
    }
}

class Portfolio {
    private final Deque<Position> requests = new ArrayDeque<>();
    private final Map<String, Position> positions = new LinkedHashMap<>();

    public void addRequest(Position tradeRequest) {
        requests.addLast(tradeRequest);
    }

    public Position executeRequest() {
        Position request = requests.pollFirst();
        if (request == null) {
            return null;
        }
        Position existing = positions.get(request.getTicker());
        if (existing == null) {
            Position newPosition = new Position(request.getInstrument(), request.getQuantity());
            positions.put(newPosition.getTicker(), newPosition);
            return newPosition;
        }
        existing.addQuantity(request.getQuantity());
        return existing;
    }

    public int getPortfolioSum() {
        int sum = 0;
        for (Position position : positions.values()) {
            sum += position.getTotalPrice();
        }
        return sum;
    }

    public Map<Instrument.Type, List<Position>> getInstrumentsByType() {
        Map<Instrument.Type, List<Position>> result = new EnumMap<>(Instrument.Type.class);
        for (Position position : positions.values()) {
            result.computeIfAbsent(position.getInstrument().getType(), key -> new ArrayList<>()).add(position);
        }
        return result;
    }

    public Set<Instrument.Type> getInstrumentTypes() {
        Set<Instrument.Type> result = EnumSet.noneOf(Instrument.Type.class);
        for (Position position : positions.values()) {
            result.add(position.getInstrument().getType());
        }
        return result;
    }
}

class Sack {
    public enum Type {
        SAND, CEMENT, PLASTER
    }

    private final Type type;
    private final int mass;

    public Sack(Type type, int mass) {
        this.type = type;
        this.mass = mass;
    }

    public Type getType() {
        return type;
    }

    public int getMass() {
        return mass;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Sack)) {
            return false;
        }
        Sack sack = (Sack) object;
        return mass == sack.mass && type == sack.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, mass);
    }

    @Override
    public String toString() {
        return "Sack{" + type + ", mass=" + mass + "}";
    }
}

class Box {
    private final int length;
    private final int width;
    private final int height;
    private final boolean fragile;

    public Box(int length, int width, int height, boolean fragile) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.fragile = fragile;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isFragile() {
        return fragile;
    }

    public int getMaxDimension() {
        return Math.max(length, Math.max(width, height));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Box)) {
            return false;
        }
        Box box = (Box) object;
        return length == box.length && width == box.width && height == box.height && fragile == box.fragile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, width, height, fragile);
    }

    @Override
    public String toString() {
        return "Box{" + length + "x" + width + "x" + height + ", fragile=" + fragile + "}";
    }
}

class Warehouse<T> {
    private final List<T> items = new ArrayList<>();
    private final Deque<T> orders = new ArrayDeque<>();

    public List<T> getItems() {
        return new ArrayList<>(items);
    }

    public void addItem(T item) {
        items.add(item);
    }

    public void addOrder(T order) {
        orders.addLast(order);
    }

    public void addVIPOrder(T order) {
        orders.addFirst(order);
    }

    public T processOrder() {
        T order = orders.pollFirst();
        if (order == null) {
            return null;
        }
        items.remove(order);
        return order;
    }
}

class SackWarehouse extends Warehouse<Sack> {
    public Map<Sack.Type, List<Sack>> getSacksByTypes() {
        Map<Sack.Type, List<Sack>> result = new EnumMap<>(Sack.Type.class);
        for (Sack sack : getItems()) {
            result.computeIfAbsent(sack.getType(), key -> new ArrayList<>()).add(sack);
        }
        return result;
    }

    public Set<Sack.Type> getEmptyTypes() {
        Set<Sack.Type> result = EnumSet.allOf(Sack.Type.class);
        for (Sack sack : getItems()) {
            result.remove(sack.getType());
        }
        return result;
    }
}

class BoxWarehouse extends Warehouse<Box> {
    public List<Box> getSortedByLength() {
        List<Box> result = getItems();
        result.sort(Comparator.comparingInt(Box::getMaxDimension));
        return result;
    }

    public int getFragileAmount() {
        int count = 0;
        for (Box box : getItems()) {
            if (box.isFragile()) {
                count++;
            }
        }
        return count;
    }
}

public class Main {
    public static void main(String[] args) {
        SackWarehouse sackWarehouse = new SackWarehouse();
        sackWarehouse.addItem(new Sack(Sack.Type.SAND, 10));
        sackWarehouse.addItem(new Sack(Sack.Type.SAND, 5));
        sackWarehouse.addItem(new Sack(Sack.Type.CEMENT, 20));
        sackWarehouse.addItem(new Sack(Sack.Type.PLASTER, 15));

        System.out.println("1. Sacks: " + sackWarehouse.getItems());

        sackWarehouse.addOrder(new Sack(Sack.Type.SAND, 10));
        sackWarehouse.addVIPOrder(new Sack(Sack.Type.CEMENT, 20));
        System.out.println("2. Orders added");

        Sack processedSack = sackWarehouse.processOrder();
        System.out.println("3. Processed: " + processedSack);
        System.out.println("4. Sacks by types: " + sackWarehouse.getSacksByTypes());
        System.out.println("Empty types: " + sackWarehouse.getEmptyTypes());

        BoxWarehouse boxWarehouse = new BoxWarehouse();
        boxWarehouse.addItem(new Box(10, 5, 5, true));
        boxWarehouse.addItem(new Box(8, 8, 2, false));
        boxWarehouse.addItem(new Box(6, 4, 3, true));

        System.out.println("5. Fragile boxes: " + boxWarehouse.getFragileAmount());

        boxWarehouse.addVIPOrder(new Box(10, 5, 5, true));
        boxWarehouse.addOrder(new Box(8, 8, 2, false));
        System.out.println("6. Box orders added");

        Box processedBox = boxWarehouse.processOrder();
        System.out.println("7. Processed: " + processedBox);
        System.out.println("8. Fragile boxes: " + boxWarehouse.getFragileAmount());
    }
}