import React from 'react';
import {Link} from 'react-router-dom';
import {
    Collapse,
    Navbar,
    NavbarToggler,
    NavbarBrand,
    Nav,
    NavItem,
    NavLink
} from 'reactstrap';

class Menu extends React.Component {
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

    getNavElement = (route, index) => {
        if (index === 0) {
            return '';
        }
        return <NavItem key={index}>
            <NavLink href={route[1]}>{route[0]}</NavLink>
        </NavItem>
    }

    render() {
        return (
            <Navbar color="light" light expand="md">
                <NavbarBrand href="/">Merlin</NavbarBrand>
                <NavbarToggler onClick={this.toggle}/>
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
