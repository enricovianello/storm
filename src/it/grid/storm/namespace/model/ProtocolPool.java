package it.grid.storm.namespace.model;

import java.util.List;
import java.util.ArrayList;

public class ProtocolPool {

  private Protocol poolType = Protocol.EMPTY;
  private String balanceStrategy;
  private List<PoolMember> poolMembers = new ArrayList<PoolMember>();

  public ProtocolPool() {
  }

  public void setBalanceStrategy(String balanceStrategy) {
    this.balanceStrategy = balanceStrategy;
  }

  public String getBalanceStrategy(){
    return this.balanceStrategy;
  }

  public void setPoolType(Protocol poolType) {
    this.poolType = poolType;
  }

  public Protocol getPoolType() {
    return this.poolType;
  }

  public void setPoolMembers(List<PoolMember> poolMembers) {
    this.poolMembers = poolMembers;
  }

  public List<PoolMember> getPoolMembers() {
    return this.poolMembers;
  }

  public void addPoolMember(PoolMember member) {
    poolMembers.add(member);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    String sep = System.getProperty("line.separator");
    sb.append(sep + "......... POOL DEFINITION ........." + sep);
    sb.append(" Balancer Strategy : "+this.balanceStrategy+sep);
    int count = 0;
    for (PoolMember member: poolMembers) {
      sb.append(" Member "+count+" = " + member + sep);
      count++;
    }
    sb.append("..................................." + sep);
    return sb.toString();
  }

}