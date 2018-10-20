import React from 'react';
import {NavLink as ReactRouterNavLink} from 'react-router-dom';
import {Collapse, Nav, Navbar, NavbarBrand, NavbarToggler, NavItem, NavLink} from 'reactstrap';

class Menu extends React.Component {
    getNavElement = (route, index) => {
        if (index === 0) {
            return '';
        }

        let addition = '';
        let className = '';

        // Additional Route Settings
        if (route.length >= 4) {
            if (route[3].badge) {
                addition = route[3].badge;
            }

            if (route[3].className) {
                className = route[3].className;
            }
        }

        return (
            <NavItem key={index}>
                <NavLink
                    to={route[1]}
                    tag={ReactRouterNavLink}
                    className={className}
                >
                    {route[0]} {addition}
                </NavLink>
            </NavItem>
        );
    };

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.state = {
            isOpen: false
        };
    }

    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }

    render() {
        return (
            <Navbar className={'fixed-top'} color="light" light expand="md">
                <NavbarBrand to="/" tag={ReactRouterNavLink}>Merlin</NavbarBrand>
                <NavbarToggler onClick={this.toggle} />
                <Collapse isOpen={this.state.isOpen} navbar>
                    <Nav className="ml-auto" navbar>
                        {
                            this.props.routes.map((route, index) => (
                                this.getNavElement(route, index)
                            ))
                        }
                    </Nav>
                </Collapse>
            </Navbar>
        );
    }

    /* <UncontrolledDropdown nav inNavbar>
         <DropdownToggle nav caret>
             Options
         </DropdownToggle>
         <DropdownMenu right>
             <DropdownItem>
                 Option 1
             </DropdownItem>
             <DropdownItem>
                 Option 2
             </DropdownItem>
             <DropdownItem divider />
             <DropdownItem>
                 Reset
             </DropdownItem>
         </DropdownMenu>
     </UncontrolledDropdown>*/
}

export default Menu;
