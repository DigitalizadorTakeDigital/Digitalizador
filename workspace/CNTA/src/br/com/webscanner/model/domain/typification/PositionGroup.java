package br.com.webscanner.model.domain.typification;

import java.util.ArrayList;
import java.util.List;

public class PositionGroup implements Recognizable{
	private List<Position> positions;

	public PositionGroup() {
		this.positions = new ArrayList<Position>();
	}
	
	public List<Position> getPositions() {
		return positions;
	}

	public boolean addPosition(Position position){
		return this.positions.add(position);
	}
}