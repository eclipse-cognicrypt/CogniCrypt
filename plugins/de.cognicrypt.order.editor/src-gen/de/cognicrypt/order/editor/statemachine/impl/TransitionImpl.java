/**
 * generated by Xtext 2.25.0
 */
package de.cognicrypt.order.editor.statemachine.impl;

import de.cognicrypt.order.editor.statemachine.Event;
import de.cognicrypt.order.editor.statemachine.State;
import de.cognicrypt.order.editor.statemachine.StatemachinePackage;
import de.cognicrypt.order.editor.statemachine.Transition;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link de.cognicrypt.order.editor.statemachine.impl.TransitionImpl#getName <em>Name</em>}</li>
 *   <li>{@link de.cognicrypt.order.editor.statemachine.impl.TransitionImpl#getFromState <em>From State</em>}</li>
 *   <li>{@link de.cognicrypt.order.editor.statemachine.impl.TransitionImpl#getEvent <em>Event</em>}</li>
 *   <li>{@link de.cognicrypt.order.editor.statemachine.impl.TransitionImpl#getEndState <em>End State</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TransitionImpl extends MinimalEObjectImpl.Container implements Transition
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getFromState() <em>From State</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFromState()
   * @generated
   * @ordered
   */
  protected State fromState;

  /**
   * The cached value of the '{@link #getEvent() <em>Event</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEvent()
   * @generated
   * @ordered
   */
  protected Event event;

  /**
   * The cached value of the '{@link #getEndState() <em>End State</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEndState()
   * @generated
   * @ordered
   */
  protected State endState;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TransitionImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return StatemachinePackage.Literals.TRANSITION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StatemachinePackage.TRANSITION__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public State getFromState()
  {
    if (fromState != null && fromState.eIsProxy())
    {
      InternalEObject oldFromState = (InternalEObject)fromState;
      fromState = (State)eResolveProxy(oldFromState);
      if (fromState != oldFromState)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, StatemachinePackage.TRANSITION__FROM_STATE, oldFromState, fromState));
      }
    }
    return fromState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public State basicGetFromState()
  {
    return fromState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFromState(State newFromState)
  {
    State oldFromState = fromState;
    fromState = newFromState;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StatemachinePackage.TRANSITION__FROM_STATE, oldFromState, fromState));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Event getEvent()
  {
    if (event != null && event.eIsProxy())
    {
      InternalEObject oldEvent = (InternalEObject)event;
      event = (Event)eResolveProxy(oldEvent);
      if (event != oldEvent)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, StatemachinePackage.TRANSITION__EVENT, oldEvent, event));
      }
    }
    return event;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Event basicGetEvent()
  {
    return event;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEvent(Event newEvent)
  {
    Event oldEvent = event;
    event = newEvent;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StatemachinePackage.TRANSITION__EVENT, oldEvent, event));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public State getEndState()
  {
    if (endState != null && endState.eIsProxy())
    {
      InternalEObject oldEndState = (InternalEObject)endState;
      endState = (State)eResolveProxy(oldEndState);
      if (endState != oldEndState)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, StatemachinePackage.TRANSITION__END_STATE, oldEndState, endState));
      }
    }
    return endState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public State basicGetEndState()
  {
    return endState;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEndState(State newEndState)
  {
    State oldEndState = endState;
    endState = newEndState;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StatemachinePackage.TRANSITION__END_STATE, oldEndState, endState));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case StatemachinePackage.TRANSITION__NAME:
        return getName();
      case StatemachinePackage.TRANSITION__FROM_STATE:
        if (resolve) return getFromState();
        return basicGetFromState();
      case StatemachinePackage.TRANSITION__EVENT:
        if (resolve) return getEvent();
        return basicGetEvent();
      case StatemachinePackage.TRANSITION__END_STATE:
        if (resolve) return getEndState();
        return basicGetEndState();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case StatemachinePackage.TRANSITION__NAME:
        setName((String)newValue);
        return;
      case StatemachinePackage.TRANSITION__FROM_STATE:
        setFromState((State)newValue);
        return;
      case StatemachinePackage.TRANSITION__EVENT:
        setEvent((Event)newValue);
        return;
      case StatemachinePackage.TRANSITION__END_STATE:
        setEndState((State)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case StatemachinePackage.TRANSITION__NAME:
        setName(NAME_EDEFAULT);
        return;
      case StatemachinePackage.TRANSITION__FROM_STATE:
        setFromState((State)null);
        return;
      case StatemachinePackage.TRANSITION__EVENT:
        setEvent((Event)null);
        return;
      case StatemachinePackage.TRANSITION__END_STATE:
        setEndState((State)null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case StatemachinePackage.TRANSITION__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case StatemachinePackage.TRANSITION__FROM_STATE:
        return fromState != null;
      case StatemachinePackage.TRANSITION__EVENT:
        return event != null;
      case StatemachinePackage.TRANSITION__END_STATE:
        return endState != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //TransitionImpl
